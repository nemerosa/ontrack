package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.job.JobHistoryService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class JobHistoryCleanupJob(
    private val jobHistoryService: JobHistoryService,
) : JobProvider {
    override fun getStartingJobs(): Collection<JobRegistration> = listOf(
        JobRegistration(
            job = createJobHistoryCleanupJob(),
            schedule = Schedule.EVERY_DAY,
        )
    )

    private fun createJobHistoryCleanupJob() = object : Job {

        override fun getKey(): JobKey =
            JobCategory.CORE.getType("jobs").withName("Jobs").getKey("history-cleanup")

        override fun getTask() = JobRun {
            jobHistoryService.cleanup()
        }

        override fun getDescription(): String = "Cleanup of job histories"

        override fun isDisabled(): Boolean = false
    }
}