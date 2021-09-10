package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.job.Job
import net.nemerosa.ontrack.job.JobKey
import net.nemerosa.ontrack.job.JobRegistration
import net.nemerosa.ontrack.job.JobRun
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

/**
 * Importing the SCM catalog entries as Ontrack projects
 */
@Component
class SCMCatalogImportJob(
    private val cachedSettingsService: CachedSettingsService,
    private val scmCatalogImportService: SCMCatalogImportService,
) : JobProvider {

    override fun getStartingJobs(): Collection<JobRegistration> = listOf(
        JobRegistration.of(
            createSCMCatalogImportJob()
        ).withSchedule(Schedule.EVERY_DAY)
    )

    private fun createSCMCatalogImportJob() = object : Job {

        override fun isDisabled(): Boolean =
            !cachedSettingsService.getCachedSettings(SCMCatalogSyncSettings::class.java).syncEnabled

        override fun getKey(): JobKey =
            SCMJobs.category
                .getType("catalog").withName("SCM Catalog")
                .getKey("import")

        override fun getDescription(): String = "Importing the SCM Catalog as projects"

        override fun getTask() = JobRun { listener ->
            scmCatalogImportService.importCatalog {
                listener.message(it)
            }
        }

    }

}