package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyType
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotator
import kotlin.reflect.KClass

abstract class AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor<T, P : PropertyType<T>>(
        private val propertyService: PropertyService,
        private val issueServiceRegistry: IssueServiceRegistry,
        private val propertyTypeClass: KClass<P>,
        private val propertyIssueServiceConfigurationIdentifier: (T) -> String?
) : FreeTextAnnotatorContributor {
    override fun getMessageAnnotators(entity: ProjectEntity): List<MessageAnnotator> {
        return listOfNotNull(getIssueMessageAnnotator(entity))
    }

    protected open fun getIssueMessageAnnotator(entity: ProjectEntity): MessageAnnotator? {
        // Gets the project
        val project: Project = entity.project
        // Gets its SCM property
        val property: T? =
                propertyService.getProperty(
                        project,
                        propertyTypeClass.java
                ).value
        // Annotator for the issue service extension
        val configuredIssueService =
                property?.let {
                    propertyIssueServiceConfigurationIdentifier(property)
                            ?.let { issueServiceConfigurationIdentifier ->
                                issueServiceRegistry.getConfiguredIssueService(
                                        issueServiceConfigurationIdentifier
                                )
                            }
                }
        return configuredIssueService?.messageAnnotator?.orElse(null)
    }
}