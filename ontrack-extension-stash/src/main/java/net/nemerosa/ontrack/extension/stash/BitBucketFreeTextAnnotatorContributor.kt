package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.support.AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BitBucketFreeTextAnnotatorContributor(
        propertyService: PropertyService,
        issueServiceRegistry: IssueServiceRegistry
) : AbstractSCMPropertyIssueBasedFreeTextAnnotatorContributor<
        StashProjectConfigurationProperty,
        StashProjectConfigurationPropertyType
        >
(
        propertyService,
        issueServiceRegistry,
        StashProjectConfigurationPropertyType::class,
        { it.issueServiceConfigurationIdentifier }
)