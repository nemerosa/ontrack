package net.nemerosa.ontrack.extension.scm.catalog.metrics

import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilterService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilterLink
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class SCMCatalogMetricsJob(
        private val scmCatalogMetricsCache: SCMCatalogMetricsCache,
        private val scmCatalogFilterService: SCMCatalogFilterService
) : JobProvider {
    override fun getStartingJobs(): Collection<JobRegistration> = setOf(
            JobRegistration(
                    createSCMCatalogMetricsJob(),
                    Schedule.EVERY_DAY
            )
    )

    private fun createSCMCatalogMetricsJob() = object : Job {

        override fun isDisabled(): Boolean = false

        override fun getKey(): JobKey = SCMJobs.category
                .getType("catalog-metrics").withName("Getting catalog metrics")
                .getKey("catalog-metrics")

        override fun getDescription(): String = "Collection of SCM Catalog metrics"

        override fun getTask() = JobRun {
            SCMCatalogProjectFilterLink.values().forEach { link ->
                val counts = scmCatalogFilterService.indexCatalogProjectEntries()
                scmCatalogMetricsCache.counts = counts
            }
        }

    }
}