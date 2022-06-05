package net.nemerosa.ontrack.extension.github.scm

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

/**
 * Implementation of the [SCM] interface for GitHub.
 */
@Component
class GitHubSCMExtension(
    gitHubExtensionFeature: GitHubExtensionFeature,
    private val propertyService: PropertyService,
    private val clientFactory: OntrackGitHubClientFactory,
) : AbstractExtension(gitHubExtensionFeature), SCMExtension {

    override fun getSCM(project: Project): SCM? {
        val property: GitHubProjectConfigurationProperty? =
            propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java).value
        return property?.run {
            GitHubSCM(
                project,
                property,
            )
        }
    }

    private inner class GitHubSCM(
        private val project: Project,
        private val property: GitHubProjectConfigurationProperty,
    ) : SCM {

        override fun getSCMBranch(branch: Branch): String? {
            checkProject(branch.project)
            val branchProperty: GitBranchConfigurationProperty? =
                propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
            return branchProperty?.branch
        }

        override fun createBranch(sourceBranch: String, newBranch: String): String {
            return client.createBranch(property.repository, sourceBranch, newBranch) ?: throw GitHubCannotCreateBranchException(
                """Cannot create branch $newBranch from $sourceBranch."""
            )
        }

        override fun download(scmBranch: String, path: String): ByteArray? =
            client.getFileContent(property.repository, scmBranch, path)

        private fun checkProject(other: Project) {
            check(other.id == project.id) {
                "An SCM service can be used only for its project."
            }
        }

        private val client: OntrackGitHubClient by lazy {
            clientFactory.create(property.configuration)
        }

    }

    private class GitHubCannotCreateBranchException(message: String) : BaseException(message)

}