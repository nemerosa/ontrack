package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class SCMCatalogJob(
    private val scmExtensionConfigProperties: SCMExtensionConfigProperties,
    private val scmCatalog: SCMCatalog
) : JobProvider {

    override fun getStartingJobs(): Collection<JobRegistration> = if (scmExtensionConfigProperties.catalog.enabled) {
        listOf(
            JobRegistration.of(
                createSCMCatalogJob()
            ).withSchedule(Schedule.EVERY_DAY)
        )
    } else {
        emptyList()
    }

    private fun createSCMCatalogJob() = object : Job {

        override fun isDisabled(): Boolean = false

        override fun getKey(): JobKey =
            SCMJobs.category
                .getType("catalog").withName("SCM Catalog")
                .getKey("collection")

        override fun getDescription(): String = "Collection of SCM Catalog"

        override fun getTask() = JobRun { listener ->
            scmCatalog.collectSCMCatalog {
                listener.message(it)
            }
        }

    }
}