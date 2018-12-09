package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.support.AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BasicGitFreeTextAnnotatorContributor(
        propertyService: PropertyService,
        issueServiceRegistry: IssueServiceRegistry
) : AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor<
        GitProjectConfigurationProperty,
        GitProjectConfigurationPropertyType
        >
(
        propertyService,
        issueServiceRegistry,
        GitProjectConfigurationPropertyType::class,
        { it.configuration.issueServiceConfigurationIdentifier }
)
