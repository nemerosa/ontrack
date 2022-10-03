package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Job which disables the orphan projects after some inactivity.
 */
@Component
class SCMOrphanDisablingJob(
    private val scmOrphanDisablingService: SCMOrphanDisablingService,
    private val cachedSettingsService: CachedSettingsService,
) : JobProvider {

    override fun getStartingJobs() = listOf(
        JobRegistration(
            createSCMOrphanDisablingJob(),
            Schedule.EVERY_WEEK.after(Duration.ofDays(7))
        )
    )

    private fun createSCMOrphanDisablingJob() = object : Job {
        override fun getKey(): JobKey = SCMJobs.category
            .getType("orphan-management").withName("Orphan management")
            .getKey("orphan-disabling")

        override fun getTask() = JobRun {
            scmOrphanDisablingService.disableOrphanProjects()
        }

        override fun getDescription(): String = "Disabling all orphan projects"

        override fun isDisabled(): Boolean =
            cachedSettingsService.getCachedSettings(SCMCatalogSyncSettings::class.java).run {
                !syncEnabled || !orphanDisablingEnabled
            }
    }
}