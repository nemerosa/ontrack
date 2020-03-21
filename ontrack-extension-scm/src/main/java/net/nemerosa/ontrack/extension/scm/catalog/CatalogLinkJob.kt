package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class CatalogLinkJob(
        private val catalogLinkService: CatalogLinkService
) : JobProvider {

    override fun getStartingJobs(): Collection<JobRegistration> = listOf(
            JobRegistration(
                    createCatalogLinkJob(),
                    Schedule.EVERY_DAY
            )
    )

    private fun createCatalogLinkJob() = object : Job {

        override fun isDisabled(): Boolean = false

        override fun getKey(): JobKey = SCMJobs.category
                .getType("catalog-link").withName("Getting catalog links")
                .getKey("catalog-link")

        override fun getDescription(): String = "Catalog links collection"

        override fun getTask() = JobRun {
            catalogLinkService.computeCatalogLinks()
        }

    }

}