package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationProperty
import net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.support.AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitLabFreeTextAnnotatorContributor(
        propertyService: PropertyService,
        issueServiceRegistry: IssueServiceRegistry
) : AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor<
        GitLabProjectConfigurationProperty,
        GitLabProjectConfigurationPropertyType
        >
(
        propertyService,
        issueServiceRegistry,
        GitLabProjectConfigurationPropertyType::class,
        { it.issueServiceConfigurationIdentifier }
)