package com.github.jonajor.branchguard.detection

import com.github.jonajor.branchguard.branch.GitBranchService
import com.github.jonajor.branchguard.settings.BranchGuardSettings
import com.github.jonajor.branchguard.ui.CreateBranchDialog
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer

class ProtectedBranchEditListener(
    private val project: Project,
    private val settings: BranchGuardSettings,
    private val sessionState: BranchGuardSessionState,
    private val gitBranchService: GitBranchService,
) : DocumentListener {
    override fun documentChanged(event: DocumentEvent) {
        val file = FileDocumentManager.getInstance().getFile(event.document) ?: return
        if (!FileEditEligibility.isEligible(project, file)) return

        val context = gitBranchService.branchContextFor(file) ?: return
        if (context.currentBranch !in settings.protectedBranchSet()) return
        if (sessionState.isSuppressed(context.repository, context.currentBranch)) return

        sessionState.suppress(context.repository, context.currentBranch)
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater
            val dialog = CreateBranchDialog(project, context.currentBranch, settings)
            if (!dialog.showAndGet()) return@invokeLater
            if (dialog.continuedAnyway()) return@invokeLater
            val branchName = dialog.branchName()
            object : Task.Backgroundable(project, "Creating branch $branchName", false) {
                override fun run(indicator: ProgressIndicator) {
                    gitBranchService.createAndCheckout(context.repository, branchName)
                }
            }.queue()
        }
    }

    companion object {
        fun install(
            project: Project,
            settings: BranchGuardSettings,
            sessionState: BranchGuardSessionState,
            gitBranchService: GitBranchService,
        ) {
            val listener = ProtectedBranchEditListener(project, settings, sessionState, gitBranchService)
            EditorFactory.getInstance().eventMulticaster.addDocumentListener(listener, project)
            Disposer.register(project) {
                EditorFactory.getInstance().eventMulticaster.removeDocumentListener(listener)
            }
        }
    }
}
