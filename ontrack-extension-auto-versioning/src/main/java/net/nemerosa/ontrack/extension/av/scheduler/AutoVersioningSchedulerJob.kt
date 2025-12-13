package net.nemerosa.ontrack.extension.av.scheduler

import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.AutoVersioningJobs
import net.nemerosa.ontrack.job.Job
import net.nemerosa.ontrack.job.JobRegistration
import net.nemerosa.ontrack.job.JobRun
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class AutoVersioningSchedulerJob(
    private val autoVersioningConfigProperties: AutoVersioningConfigProperties,
    private val autoVersioningScheduler: AutoVersioningScheduler,
) : JobProvider {

    override fun getStartingJobs(): Collection<JobRegistration> =
        listOf(
            JobRegistration(
                job = createJob(),
                schedule = Schedule.cron(autoVersioningConfigProperties.scheduling.cron)
            )
        )

    private fun createJob() = object : Job {
        override fun getKey() =
            AutoVersioningJobs.category
                .getType("auto-versioning-scheduler")
                .withName("Auto-versioning scheduler")
                .getKey("main")

        override fun getTask() = JobRun { listener ->
            if (autoVersioningConfigProperties.scheduling.enabled) {
                autoVersioningScheduler.schedule()
            } else {
                listener.message("Auto-versioning scheduling is disabled")
            }
        }

        override fun getDescription(): String = "Auto-versioning scheduler"

        override fun isDisabled(): Boolean = false
    }
}