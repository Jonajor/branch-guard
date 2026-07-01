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
        if (sessionState.isPromptDismissed(context.repository, context.currentBranch)) return
        if (!sessionState.markPromptActive(context.repository, context.currentBranch)) return

        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) {
                sessionState.clearPromptActive(context.repository, context.currentBranch)
                return@invokeLater
            }

            val dialog = CreateBranchDialog(project, context.currentBranch, settings)
            if (!dialog.showAndGet() || dialog.continuedAnyway()) {
                sessionState.dismissPrompt(context.repository, context.currentBranch)
                sessionState.clearPromptActive(context.repository, context.currentBranch)
                return@invokeLater
            }

            val branchName = dialog.branchName()
            object : Task.Backgroundable(project, "Creating branch $branchName", false) {
                override fun run(indicator: ProgressIndicator) {
                    try {
                        gitBranchService.createAndCheckout(context.repository, branchName)
                    } finally {
                        sessionState.clearPromptActive(context.repository, context.currentBranch)
                    }
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
