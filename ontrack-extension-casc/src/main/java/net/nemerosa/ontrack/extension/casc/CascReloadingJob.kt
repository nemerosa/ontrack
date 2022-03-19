package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

/**
 * Job to regularly reload the CasC locations.
 */
@Component
class CascReloadingJob(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val cascLoadingService: CascLoadingService,
) : JobProvider {
    override fun getStartingJobs() =
        if (cascConfigurationProperties.enabled && cascConfigurationProperties.reloading.enabled) {
            listOf(
                JobRegistration(
                    createCascReloadingJob(),
                    if (cascConfigurationProperties.reloading.cron.isNotBlank()) {
                        Schedule.cron(cascConfigurationProperties.reloading.cron)
                    } else {
                        Schedule.NONE
                    }
                )
            )
        } else {
            emptyList()
        }

    private fun createCascReloadingJob() = object : Job {

        override fun getKey(): JobKey =
            JobCategory.CORE.getType("casc").withName("CasC")
                .getKey("reloading")

        override fun getTask() = JobRun {
            cascLoadingService.load()
        }

        override fun getDescription(): String = "Reloading the Casc configuration"

        override fun isDisabled(): Boolean = !cascConfigurationProperties.enabled
    }
}