package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.AutoVersioningJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class AutoVersioningAuditCleanupJob(
    private val autoVersioningAuditCleanupService: AutoVersioningAuditCleanupService,
) : JobProvider {

    override fun getStartingJobs() = listOf(
        JobRegistration(
            createAutoVersioningAuditCleanupJob(),
            Schedule.EVERY_DAY
        )
    )

    private fun createAutoVersioningAuditCleanupJob() = object : Job {

        override fun getKey(): JobKey =
            AutoVersioningJobs.category
                .getType("auto-versioning-audit-cleanup")
                .withName("Auto versioning audit cleanup")
                .getKey("main")

        override fun getTask() = JobRun { listener ->
            val count = autoVersioningAuditCleanupService.cleanup()
            listener.message("Removed $count audit entries")
        }

        override fun getDescription(): String = "Cleanup of old auto versioning requests audit entries"

        override fun isDisabled(): Boolean = false

    }

}