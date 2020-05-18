package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.model.IndicatorComputer
import net.nemerosa.ontrack.extension.indicators.model.id
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class IndicatorComputingJobs(
        private val structureService: StructureService,
        private val computers: List<IndicatorComputer>,
        private val indicatorComputingService: IndicatorComputingService
) : JobOrchestratorSupplier {

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
            compute(computer, project)
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
                        computer.isProjectEligible(it)
                    }
                    .forEach { project ->
                        compute(computer, project)
                    }
        }
    }

    private fun compute(computer: IndicatorComputer, project: Project) {
        indicatorComputingService.compute(computer, project)
    }

    private fun getJobType(computer: IndicatorComputer): JobType =
            CATEGORY.getType(computer.id).withName("Indicator computing for ${computer.name}")

    companion object {
        val CATEGORY: JobCategory = JobCategory.of("indicator-computing").withName("Indicator computing")
    }

}