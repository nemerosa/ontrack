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
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
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
            return client.createBranch(property.repository, sourceBranch, newBranch)
                ?: throw GitHubCannotCreateBranchException(
                    """Cannot create branch $newBranch from $sourceBranch."""
                )
        }

        override fun download(scmBranch: String, path: String): ByteArray? =
            client.getFileContent(property.repository, scmBranch, path)

        override fun upload(scmBranch: String, commit: String, path: String, content: ByteArray) {
            // First, we need the SHA of the file
            val sha = client.getFile(property.repository, scmBranch, path)
                ?.sha
                ?: error("Cannot find file at $path for branch $scmBranch")
            client.setFileContent(property.repository, scmBranch, sha, path, content)
        }

        /**
         * See https://developer.github.com/v3/pulls/#create-a-pull-request
         */
        override fun createPR(
            from: String,
            to: String,
            title: String,
            description: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean
        ): SCMPullRequest {
            // Creates the pull request
            val pr = client.createPR(
                title = title,
                head = from,
                base = to,
                body = description
            )
            val prId = pr.number
            // Auto approval process (approval + wait for checks + merge)
            var merged = false
            // TODO Auto approval
            // if (autoApproval) {
            //     // Approving using the auto merge account
            //     val autoApprovalClient = client.withNewAuthentication(
            //         username = prCreationSettings.gitHubAutoApprovalUsername,
            //         password = prCreationSettings.gitHubAutoApprovalPassword ?: ""
            //     )
            //     autoApprovalClient.approvePR(pr.number, "Automated review for auto versioning on promotion")
            //     // Auto merge
            //     if (remoteAutoMerge) {
            //         client.enableAutoMerge(pr.number)
            //     } else {
            //         merged = waitAndMerge(prId)
            //     }
            // }
            // PR
            return SCMPullRequest(
                id = pr.number.toString(),
                name = "#${pr.number}",
                link = pr.html_url ?: "",
                merged = merged
            )
        }

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