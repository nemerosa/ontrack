package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.extension.hook.HookJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class HookRecordCleanupJob(
        private val hookRecordCleanupService: HookRecordCleanupService,
) : JobProvider {

    override fun getStartingJobs() = listOf(
            JobRegistration(
                    createHookRecordCleanupJob(),
                    Schedule.EVERY_DAY
            )
    )

    private fun createHookRecordCleanupJob() = object : Job {

        override fun getKey(): JobKey =
                HookJobs.category
                        .getType("hook-record-cleanup")
                        .withName("hook record cleanup")
                        .getKey("main")

        override fun getTask() = JobRun { listener ->
            val count = hookRecordCleanupService.cleanup()
            listener.message("Removed $count records")
        }

        override fun getDescription(): String = "Cleanup of old hook message records"

        override fun isDisabled(): Boolean = false

    }

}