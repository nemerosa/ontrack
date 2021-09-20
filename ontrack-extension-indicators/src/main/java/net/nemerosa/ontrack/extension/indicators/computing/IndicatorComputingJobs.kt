package net.nemerosa.ontrack.extension.indicators.computing

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputingMetrics.METRIC_ONTRACK_INDICATORS_COMPUTING_MS
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.time
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class IndicatorComputingJobs(
    private val structureService: StructureService,
    private val computers: List<IndicatorComputer>,
    private val indicatorComputingService: IndicatorComputingService,
    private val meterRegistry: MeterRegistry,
    private val applicationLogService: ApplicationLogService,
) : JobOrchestratorSupplier {

    private val logger: Logger = LoggerFactory.getLogger(IndicatorComputingJobs::class.java)

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        // Gets the list of projects
        val projects = structureService.projectList
        // Creates the list of jobs
        return computers.flatMap { computer ->
            createJobRegistrations(computer, projects)
        }.stream()
    }

    private fun createJobRegistrations(computer: IndicatorComputer, projects: List<Project>): List<JobRegistration> {
        return if (computer.perProject) {
            projects
                    .filter { computer.isProjectEligible(it) }
                    .map { project ->
                        JobRegistration(
                                job = createJob(computer, project),
                                schedule = computer.schedule
                        )
                    }
        } else {
            listOf(
                    JobRegistration(
                            job = createJob(computer, projects),
                            schedule = computer.schedule
                    )
            )
        }
    }

    private fun createJob(computer: IndicatorComputer, project: Project) = object : Job {

        override fun isDisabled(): Boolean = project.isDisabled

        override fun getKey(): JobKey = getJobType(computer).getKey(project.name)

        override fun getDescription(): String =
                "Computing indicator values by ${computer.name} for project ${project.name}"

        override fun getTask() = JobRun {
            compute(computer, project, allowFailure = true)
        }
    }

    private fun createJob(computer: IndicatorComputer, projects: List<Project>) = object : Job {

        override fun isDisabled(): Boolean = false

        override fun getKey(): JobKey = getJobType(computer).getKey("all")

        override fun getDescription(): String =
                "Computing indicator values by ${computer.name} for all projects"

        override fun getTask() = JobRun {
            projects
                    .filter {
                        !it.isDisabled && computer.isProjectEligible(it)
                    }
                    .forEach { project ->
                        compute(computer, project, allowFailure = false)
                    }
        }
    }

    private fun compute(computer: IndicatorComputer, project: Project, allowFailure: Boolean) {
        meterRegistry.time(
                METRIC_ONTRACK_INDICATORS_COMPUTING_MS,
                "computer" to computer.id,
                "project" to project.name
        ) {
            try {
                logger.info("[indicator-computing] computer=${computer.id},project=${project.name},start")
                indicatorComputingService.compute(computer, project)
                logger.info("[indicator-computing] computer=${computer.id},project=${project.name},end")
            } catch (any: Exception) {
                if (allowFailure) {
                    throw any
                } else {
                    // Does not stop the job
                    // Just logs the error
                    applicationLogService.log(
                        ApplicationLogEntry.error(
                            any,
                            NameDescription.nd("indicator-computing-error", "Indicator computation error"),
                            "Error while computing ${computer.name} for project ${project.name}"
                        )
                            .withDetail("computer", computer.id)
                            .withDetail("project", project.name)
                    )
                    // Going on...
                }
            }
        }
    }

    private fun getJobType(computer: IndicatorComputer): JobType =
            CATEGORY.getType(computer.id).withName("Indicator computing for ${computer.name}")

    companion object {
        val CATEGORY: JobCategory = JobCategory.of("indicator-computing").withName("Indicator computing")
    }

}