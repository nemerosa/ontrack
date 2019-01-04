package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component
import java.util.stream.Stream

/**
 * Orchestrates the collection of labels for all projects.
 */
@Component
class LabelProviderJob(
        private val securityService: SecurityService,
        private val structureService: StructureService,
        private val labelProviderService: LabelProviderService,
        private val ontrackConfigProperties: OntrackConfigProperties
) : JobOrchestratorSupplier {

    companion object {
        val LABEL_PROVIDER_JOB_TYPE = JobCategory.CORE
                .getType("label-provider").withName("Label Provider")
    }

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        return securityService.callAsAdmin {
            structureService.projectList
                    .map { createLabelProviderJobRegistration(it) }
                    .stream()
        }
    }

    private fun createLabelProviderJobRegistration(project: Project): JobRegistration {
        return JobRegistration
                .of(createLabelProviderJob(project))
                .withSchedule(Schedule.everyMinutes(60)) // Hourly
    }

    private fun createLabelProviderJob(project: Project): Job {
        return object : Job {
            override fun getKey(): JobKey =
                    LABEL_PROVIDER_JOB_TYPE.getKey(project.name)

            override fun getTask() = JobRun {
                securityService.asAdmin {
                    labelProviderService.collectLabels(project)
                }
            }

            override fun getDescription(): String =
                    "Collection of automated labels for project ${project.name}"

            override fun isDisabled(): Boolean = project.isDisabled || !ontrackConfigProperties.isJobLabelProviderEnabled

        }
    }
}