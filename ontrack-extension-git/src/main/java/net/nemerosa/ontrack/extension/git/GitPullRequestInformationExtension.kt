package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.git.model.getBranchAsPullRequest
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import java.net.URI

@Component
class GitPullRequestInformationExtension(
        extensionFeature: GitExtensionFeature,
        private val propertyService: PropertyService,
        private val uriBuilder: URIBuilder,
        private val gitService: GitService
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): EntityInformation? =
            if (entity is Branch) {
                val property: GitBranchConfigurationProperty? = propertyService.getProperty(entity, GitBranchConfigurationPropertyType::class.java).value
                val pr = gitService.getBranchAsPullRequest(entity, property)
                pr?.let {
                    EntityInformation(this, GitPullRequestInformationExtensionData(
                            pr = pr,
                            sourceBranchPage = gitService.findBranchWithGitBranch(entity.project, pr.source)
                                    ?.let { uriBuilder.getEntityPage(it) },
                            targetBranchPage = gitService.findBranchWithGitBranch(entity.project, pr.target)
                                    ?.let { uriBuilder.getEntityPage(it) }
                    ))
                }
            } else {
                null
            }

    class GitPullRequestInformationExtensionData(
            val pr: GitPullRequest,
            val sourceBranchPage: URI?,
            val targetBranchPage: URI?
    )

}