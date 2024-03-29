package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.LabelProviderJobSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Orchestrates the collection of labels for all projects.
 */
@Component
class LabelProviderJob(
        private val securityService: SecurityService,
        private val structureService: StructureService,
        private val labelProviderService: LabelProviderService,
        private val settingsService: CachedSettingsService
) : JobOrchestratorSupplier {

    companion object {
        val LABEL_PROVIDER_JOB_TYPE = JobCategory.CORE
                .getType("label-provider").withName("Label Provider")
    }

    override val jobRegistrations: Collection<JobRegistration> get() {
        val settings: LabelProviderJobSettings = settingsService.getCachedSettings(LabelProviderJobSettings::class.java)
        return if (settings.enabled) {
            if (settings.perProject) {
                securityService.asAdmin {
                    structureService.projectList
                            .map { createLabelProviderJobRegistrationForProject(it, settings) }
                }
            } else {
                listOf(createLabelProviderJobRegistration(settings))
            }
        } else {
            emptyList()
        }
    }

    private fun createLabelProviderJobRegistration(settings: LabelProviderJobSettings): JobRegistration =
            JobRegistration
                    .of(createLabelProviderJob())
                    .withSchedule(Schedule.everyMinutes(settings.interval.toLong()))

    private fun createLabelProviderJob(): Job = object : Job {
        override fun getKey(): JobKey =
                LABEL_PROVIDER_JOB_TYPE.getKey("label-collection")

        override fun getTask() = JobRun {
            securityService.asAdmin {
                structureService.projectList.forEach { project ->
                    if (!project.isDisabled) {
                        labelProviderService.collectLabels(project)
                    }
                }
            }
        }

        override fun getDescription(): String =
                "Collection of automated labels for all projects"

        override fun isDisabled(): Boolean = false

    }

    private fun createLabelProviderJobRegistrationForProject(project: Project, settings: LabelProviderJobSettings): JobRegistration {
        return JobRegistration
                .of(createLabelProviderJobForProject(project))
                .withSchedule(Schedule.everyMinutes(settings.interval.toLong()))
    }

    private fun createLabelProviderJobForProject(project: Project): Job {
        return object : Job {
            override fun getKey(): JobKey =
                    LABEL_PROVIDER_JOB_TYPE.getKey(project.name)

            override fun getTask() = JobRun {
                if (!project.isDisabled) {
                    securityService.asAdmin {
                        labelProviderService.collectLabels(project)
                    }
                }
            }

            override fun getDescription(): String =
                    "Collection of automated labels for project ${project.name}"

            override fun isDisabled(): Boolean = project.isDisabled

        }
    }
}