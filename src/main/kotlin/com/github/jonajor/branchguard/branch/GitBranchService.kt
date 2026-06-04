package com.github.jonajor.branchguard.branch

import com.github.jonajor.branchguard.notifications.BranchGuardNotifications
import com.intellij.notification.NotificationAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

class GitBranchService(private val project: Project) {
    data class BranchContext(
        val repository: GitRepository,
        val currentBranch: String,
    )

    fun branchContextFor(file: VirtualFile): BranchContext? {
        val repository = GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(file) ?: return null
        val branch = repository.currentBranchName ?: return null
        return BranchContext(repository, branch)
    }

    fun createAndCheckout(repository: GitRepository, branchName: String) {
        if (branchExists(repository, branchName)) {
            BranchGuardNotifications.warning(
                project,
                "Branch already exists",
                branchName,
                NotificationAction.createSimple("Checkout Existing Branch") {
                    checkoutExisting(repository, branchName)
                },
            )
            return
        }

        val handler = GitLineHandler(project, repository.root, GitCommand.CHECKOUT).apply {
            addParameters("-b", branchName)
        }
        val result = Git.getInstance().runCommand(handler)
        if (result.success()) {
            repository.update()
            BranchGuardNotifications.success(project, "Branch Guard", "Created and checked out:<br/>$branchName")
        } else {
            BranchGuardNotifications.error(
                project,
                "Could not create branch",
                result.errorOutputAsHtmlString.ifBlank { "Git checkout failed for $branchName" },
            )
        }
    }

    private fun checkoutExisting(repository: GitRepository, branchName: String) {
        val handler = GitLineHandler(project, repository.root, GitCommand.CHECKOUT).apply {
            addParameters(branchName)
        }
        val result = Git.getInstance().runCommand(handler)
        if (result.success()) {
            repository.update()
            BranchGuardNotifications.success(project, "Branch Guard", "Checked out:<br/>$branchName")
        } else {
            BranchGuardNotifications.error(
                project,
                "Could not checkout branch",
                result.errorOutputAsHtmlString.ifBlank { "Git checkout failed for $branchName" },
            )
        }
    }

    private fun branchExists(repository: GitRepository, branchName: String): Boolean =
        repository.branches.localBranches.any { it.name == branchName }
}
