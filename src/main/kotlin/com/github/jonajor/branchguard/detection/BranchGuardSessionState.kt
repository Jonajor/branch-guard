package com.github.jonajor.branchguard.detection

import git4idea.repo.GitRepository
import java.util.concurrent.ConcurrentHashMap

class BranchGuardSessionState {
    private val activePrompts = ConcurrentHashMap.newKeySet<String>()

    fun markPromptActive(repository: GitRepository, branch: String): Boolean =
        activePrompts.add(key(repository, branch))

    fun clearPromptActive(repository: GitRepository, branch: String) {
        activePrompts.remove(key(repository, branch))
    }

    fun reset() {
        activePrompts.clear()
    }

    private fun key(repository: GitRepository, branch: String): String =
        "${repository.root.path}:$branch"
}
