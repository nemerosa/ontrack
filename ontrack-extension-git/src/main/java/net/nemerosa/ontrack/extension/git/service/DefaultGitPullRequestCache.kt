package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.GitConfigProperties
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.EntityDataService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DefaultGitPullRequestCache(
    private val entityDataService: EntityDataService,
    private val gitConfigProperties: GitConfigProperties,
) : GitPullRequestCache {

    override fun getBranchPullRequest(branch: Branch, prProvider: () -> GitPullRequest?): GitPullRequest? {
        return if (gitConfigProperties.pullRequests.cache.enabled) {
            prProvider()
        } else {
            val now = Time.now()
            // Gets any existing PR for the branch
            val pr =
                entityDataService.retrieve(branch, GitPullRequest::class.java.name, StoredGitPullRequest::class.java)
            return if (pr != null && pr.expirationTime > now) {
                pr.pr
            } else {
                val reloadedPr = prProvider()
                reloadedPr?.apply {
                    entityDataService.store(
                        branch,
                        GitPullRequest::class.java.name,
                        StoredGitPullRequest(
                            pr = this,
                            expirationTime = now + gitConfigProperties.pullRequests.cache.duration
                        )
                    )
                }
            }
        }
    }

    /**
     * Internal storage including registration time.
     */
    private class StoredGitPullRequest(
        val pr: GitPullRequest,
        val expirationTime: LocalDateTime,
    )

}