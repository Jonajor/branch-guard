package com.github.jonajor.branchguard.settings

import com.github.jonajor.branchguard.branch.GitBranchService
import com.github.jonajor.branchguard.detection.BranchChangeListener
import com.github.jonajor.branchguard.detection.BranchGuardSessionState
import com.github.jonajor.branchguard.detection.ProtectedBranchEditListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import git4idea.repo.GitRepository

class BranchGuardStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val settings = BranchGuardSettings.getInstance()
        val sessionState = BranchGuardSessionState()
        val gitBranchService = GitBranchService(project)

        ProtectedBranchEditListener.install(project, settings, sessionState, gitBranchService)

        project.messageBus.connect(project).subscribe(
            GitRepository.GIT_REPO_CHANGE,
            BranchChangeListener(project, sessionState),
        )

        if (!settings.setupCompleted) {
            ApplicationManager.getApplication().invokeLater {
                if (!project.isDisposed && !settings.setupCompleted) {
                    InitialSetupDialog(project, settings).show()
                }
            }
        }
    }
}
