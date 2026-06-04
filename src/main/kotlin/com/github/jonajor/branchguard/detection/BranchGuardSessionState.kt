package com.github.jonajor.branchguard.detection

import git4idea.repo.GitRepository
import java.util.concurrent.ConcurrentHashMap

class BranchGuardSessionState {
    private val suppressed = ConcurrentHashMap.newKeySet<String>()

    fun isSuppressed(repository: GitRepository, branch: String): Boolean =
        key(repository, branch) in suppressed

    fun suppress(repository: GitRepository, branch: String) {
        suppressed.add(key(repository, branch))
    }

    fun reset() {
        suppressed.clear()
    }

    private fun key(repository: GitRepository, branch: String): String =
        "${repository.root.path}:$branch"
}
