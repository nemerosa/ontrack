package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.model.structure.Branch

/**
 * Caching the pull requests for the branches.
 */
interface GitPullRequestCache {

    /**
     * Gets a PR for a branch from the cache if not empty and valid, and loads it if not available.
     */
    fun getBranchPullRequest(
        branch: Branch,
        prProvider: () -> GitPullRequest?,
    ): GitPullRequest?

}