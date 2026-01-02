package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsJob
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.*

/**
 * Provides a decoration that displays the state of a running job.
 */
@Component
class JenkinsJobDecorationExtension(
    extensionFeature: JenkinsExtensionFeature,
    private val propertyService: PropertyService,
    private val jenkinsClientFactory: JenkinsClientFactory
) : AbstractExtension(extensionFeature), DecorationExtension<JenkinsJob> {

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(
            ProjectEntityType.PROJECT,
            ProjectEntityType.BRANCH,
            ProjectEntityType.PROMOTION_LEVEL,
            ProjectEntityType.VALIDATION_STAMP
        )
    }

    override fun getDecorations(entity: ProjectEntity): List<Decoration<JenkinsJob>> {
        // Gets the Jenkins Job property for this entity, if any
        return propertyService.getPropertyValue(entity, JenkinsJobPropertyType::class.java)
            ?.let {
                // Gets a client
                val jenkinsClient = jenkinsClientFactory.getClient(it.configuration)
                // Gets the Jenkins job
                val job = jenkinsClient.getJob(it.job)
                // Gets the decoration for the job
                listOf(
                    getDecoration(job)
                )
            }
            ?: emptyList()
    }

    private fun getDecoration(job: JenkinsJob): Decoration<JenkinsJob> {
        return Decoration.of(this, job)
    }
}
