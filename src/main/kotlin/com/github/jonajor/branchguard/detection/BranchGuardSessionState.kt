package com.github.jonajor.branchguard.detection

import git4idea.repo.GitRepository
import java.util.concurrent.ConcurrentHashMap

class BranchGuardSessionState {
    private val activePrompts = ConcurrentHashMap.newKeySet<String>()
    private val dismissedPrompts = ConcurrentHashMap.newKeySet<String>()

    fun isPromptDismissed(repository: GitRepository, branch: String): Boolean =
        key(repository, branch) in dismissedPrompts

    fun markPromptActive(repository: GitRepository, branch: String): Boolean =
        activePrompts.add(key(repository, branch))

    fun clearPromptActive(repository: GitRepository, branch: String) {
        activePrompts.remove(key(repository, branch))
    }

    fun dismissPrompt(repository: GitRepository, branch: String) {
        dismissedPrompts.add(key(repository, branch))
    }

    fun reset() {
        activePrompts.clear()
        dismissedPrompts.clear()
    }

    private fun key(repository: GitRepository, branch: String): String =
        "${repository.root.path}:$branch"
}
