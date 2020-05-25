package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.RestorationJobs
import org.springframework.stereotype.Component

/**
 * Job to re-export all validation run data metrics.
 */
@Component
class ValidationRunDataMetricsRestorationJob(
        private val structureService: StructureService
) : JobProvider, Job {

    override fun getStartingJobs(): Collection<JobRegistration> = listOf(
            JobRegistration(
                    this,
                    Schedule.NONE
            )
    )

    override fun isDisabled(): Boolean = false

    override fun getKey(): JobKey =
            RestorationJobs.RESTORATION_JOB_TYPE.getKey("validation-run-data-metrics-restoration")

    override fun getDescription(): String = "Restoration of validation run data metrics"

    override fun getTask() = JobRun { listener ->
        structureService.restoreValidationRunDataMetrics {
            listener.message(it)
        }
    }

}