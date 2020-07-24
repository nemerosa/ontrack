package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.git.model.getBranchAsPullRequest
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitPullRequestInformationExtension(
        extensionFeature: GitExtensionFeature,
        private val propertyService: PropertyService,
        private val gitService: GitService
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): EntityInformation? =
            if (entity is Branch) {
                val property: GitBranchConfigurationProperty? = propertyService.getProperty(entity, GitBranchConfigurationPropertyType::class.java).value
                val pr = gitService.getBranchAsPullRequest(entity, property)
                pr?.let {
                    EntityInformation(this, pr.simplifyBranchNames())
                }
            } else {
                null
            }

}