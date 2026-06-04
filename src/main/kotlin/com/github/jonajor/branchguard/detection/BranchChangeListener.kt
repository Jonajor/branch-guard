package com.github.jonajor.branchguard.detection

import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener

class BranchChangeListener(
    private val project: Project,
    private val sessionState: BranchGuardSessionState,
) : GitRepositoryChangeListener {
    private val knownBranches = mutableMapOf<String, String?>()

    override fun repositoryChanged(repository: GitRepository) {
        if (project.isDisposed) return
        val key = repository.root.path
        val previous = knownBranches.put(key, repository.currentBranchName)
        if (previous != null && previous != repository.currentBranchName) {
            sessionState.reset()
        }
    }
}
