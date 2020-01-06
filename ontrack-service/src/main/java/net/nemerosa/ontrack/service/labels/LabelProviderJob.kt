package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.util.stream.Stream
import kotlin.streams.asStream

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

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        val settings: LabelProviderJobSettings = settingsService.getCachedSettings(LabelProviderJobSettings::class.java)
        return if (settings.enabled) {
            securityService.callAsAdmin {
                structureService.projectList
                        .map { createLabelProviderJobRegistration(it, settings) }
                        .stream()
            }
        } else {
            emptySequence<JobRegistration>().asStream()
        }
    }

    private fun createLabelProviderJobRegistration(project: Project, settings: LabelProviderJobSettings): JobRegistration {
        return JobRegistration
                .of(createLabelProviderJob(project))
                .withSchedule(Schedule.everyMinutes(settings.interval.toLong()))
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

            override fun isDisabled(): Boolean = project.isDisabled

        }
    }
}