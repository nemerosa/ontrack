package net.nemerosa.ontrack.extension.git.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.GitConfigProperties
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.EntityDataService
import net.nemerosa.ontrack.model.support.time
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DefaultGitPullRequestCache(
    private val entityDataService: EntityDataService,
    private val gitConfigProperties: GitConfigProperties,
) : GitPullRequestCache, MeterBinder {

    private lateinit var meterRegistry: MeterRegistry

    override fun bindTo(registry: MeterRegistry) {
        this.meterRegistry = registry
        registry.gauge(GitPullRequestCacheMetrics.git_pr_cache_count, this) {
            entityDataService.countByKey(GitPullRequest::class.java.name).toDouble()
        }
    }

    override fun getBranchPullRequest(branch: Branch, prProvider: () -> GitPullRequest?): GitPullRequest? {
        return if (gitConfigProperties.pullRequests.cache.enabled) {
            prProvider()
        } else {
            meterRegistry.time(GitPullRequestCacheMetrics.git_pr_cache_time_all) {
                val now = Time.now()
                // Gets any existing PR for the branch
                val pr =
                    entityDataService.retrieve(
                        branch,
                        GitPullRequest::class.java.name,
                        StoredGitPullRequest::class.java
                    )
                if (pr != null && pr.expirationTime > now) {
                    meterRegistry.increment(GitPullRequestCacheMetrics.git_pr_cache_hits)
                    pr.pr
                } else {
                    meterRegistry.increment(GitPullRequestCacheMetrics.git_pr_cache_miss)
                    val reloadedPr = meterRegistry.time(GitPullRequestCacheMetrics.git_pr_cache_time_scm) {
                        prProvider()
                    }
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
    }

    /**
     * Internal storage including registration time.
     */
    private class StoredGitPullRequest(
        val pr: GitPullRequest,
        val expirationTime: LocalDateTime,
    )

}