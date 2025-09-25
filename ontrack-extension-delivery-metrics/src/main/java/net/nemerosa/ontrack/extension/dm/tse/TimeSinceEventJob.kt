package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.extension.dm.model.DeliveryMetricsJobs
import net.nemerosa.ontrack.extension.scm.branching.BranchingModelPropertyType
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * This job computes all "time since events" of main branches and exports the metrics.
 */
@Component
class TimeSinceEventJob(
        private val structureService: StructureService,
        private val propertyService: PropertyService,
        private val timeSinceEventService: TimeSinceEventService,
        private val timeSinceEventConfigurationProperties: TimeSinceEventConfigurationProperties,
) : JobOrchestratorSupplier {

    override val jobRegistrations: Collection<JobRegistration>
        get() = structureService.projectList
                .filter { it.isEligibleForTSE }
                .map { it.createTimeSinceEventJobRegistration() }

    private val Project.isEligibleForTSE: Boolean
        get() = !isDisabled && propertyService.hasProperty(this, BranchingModelPropertyType::class.java)

    private fun Project.createTimeSinceEventJobRegistration() = JobRegistration(
            createTimeSinceEventJob(),
            Schedule.everySeconds(timeSinceEventConfigurationProperties.interval.toSeconds())
    )

    private fun Project.createTimeSinceEventJob() = object : Job {

        override fun isDisabled(): Boolean = this@createTimeSinceEventJob.isDisabled

        override fun getKey(): JobKey =
                DeliveryMetricsJobs.DM_JOB_CATEGORY
                        .getType("time-since-event").withName("Time since events")
                        .getKey(name) // Project name as key

        override fun getDescription(): String =
                """$name time since events"""

        override fun getTask(): JobRun = JobRun { listener ->
            timeSinceEventService.collectTimesSinceEvents(this@createTimeSinceEventJob) {
                listener.message(it)
            }
        }
    }

}
