package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

/**
 * Job used to re-export all run infos into registered listeners.
 */
@Component
class RunInfoRestorationJob(
        private val runInfoService: RunInfoService
): JobProvider, Job {

    override fun getStartingJobs(): Collection<JobRegistration> =
            listOf(
                    JobRegistration(
                            this,
                            Schedule.NONE // Manually only
                    )
            )

    override fun isDisabled(): Boolean = false

    override fun getKey(): JobKey =
            JobCategory.CORE.getType("run-info-restoration").withName("Run Info Restoration").getKey("0")

    override fun getDescription(): String = "Run Info Restoration"

    override fun getTask() = JobRun { listener ->
        runInfoService.restore {
            listener.message(it)
        }
    }

}