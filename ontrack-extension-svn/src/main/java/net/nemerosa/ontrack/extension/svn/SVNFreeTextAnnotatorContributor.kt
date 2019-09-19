package net.nemerosa.ontrack.extension.svn

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.support.AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class SVNFreeTextAnnotatorContributor(
        propertyService: PropertyService,
        issueServiceRegistry: IssueServiceRegistry
) : AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor<
        SVNProjectConfigurationProperty,
        SVNProjectConfigurationPropertyType
        >
(
        propertyService,
        issueServiceRegistry,
        SVNProjectConfigurationPropertyType::class,
        { it.configuration.issueServiceConfigurationIdentifier }
)
