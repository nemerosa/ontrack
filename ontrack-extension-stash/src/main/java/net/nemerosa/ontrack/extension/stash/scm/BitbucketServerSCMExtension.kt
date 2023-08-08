package net.nemerosa.ontrack.extension.stash.scm

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.stash.StashExtensionFeature
import net.nemerosa.ontrack.extension.stash.client.BitbucketClient
import net.nemerosa.ontrack.extension.stash.client.BitbucketClientFactory
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.extension.stash.property.StashConfigurator
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.settings.BitbucketServerSettings
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BitbucketServerSCMExtension(
    extensionFeature: StashExtensionFeature,
    private val propertyService: PropertyService,
    private val stashConfigurator: StashConfigurator,
    private val bitbucketClientFactory: BitbucketClientFactory,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractExtension(extensionFeature), SCMExtension {

    override fun getSCM(project: Project): SCM? {
        val property =
            propertyService.getPropertyValue(project, StashProjectConfigurationPropertyType::class.java) ?: return null
        return BitbucketServerSCM(project, property, cachedSettingsService.getCachedSettings(BitbucketServerSettings::class.java))
    }

    private inner class BitbucketServerSCM(
        private val project: Project,
        private val property: StashProjectConfigurationProperty,
        private val settings: BitbucketServerSettings,
    ) : SCM {

        private val stashConfiguration = stashConfigurator.getGitConfiguration(property)

        private val repo = BitbucketRepository(property.project, property.repository)

        override val repositoryURI: String = stashConfiguration.remote

        override val repositoryHtmlURL: String = property.repositoryUrl

        override val repository: String = "${property.project}/${property.repository}"

        override fun getSCMBranch(branch: Branch): String? {
            checkProject(branch.project)
            val branchProperty: GitBranchConfigurationProperty? =
                propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
            return branchProperty?.branch
        }

        override fun createBranch(sourceBranch: String, newBranch: String): String =
            client.createBranch(repo, sourceBranch, newBranch)

        override fun download(scmBranch: String, path: String, retryOnNotFound: Boolean): ByteArray? =
            client.download(repo, scmBranch, path)

        override fun upload(scmBranch: String, commit: String, path: String, content: ByteArray, message: String) {
            client.upload(
                repo = repo,
                branch = scmBranch,
                commit = commit,
                path = path,
                content = content,
                message = message
            )
        }

        override fun createPR(
            from: String,
            to: String,
            title: String,
            description: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean,
            message: String
        ): SCMPullRequest {
            // Creates the pull request
            val pr = client.createPR(
                repo = repo,
                title = title,
                head = from,
                base = to,
                body = description
            )
            val prId = pr.id
            // Auto approval process (approval + wait for checks + merge)
            var merged = false
            // Auto approval
            if (autoApproval) {
                // Auto merge token must be set
                if (property.configuration.autoMergeToken.isNullOrBlank()) {
                    throw BitbucketServerSCMMissingAutoMergeTokenException(property.configuration.name)
                }
                if (property.configuration.autoMergeUser.isNullOrBlank()) {
                    throw BitbucketServerSCMMissingAutoMergeUserException(property.configuration.name)
                }
                // Approving using the auto merge account
                client.approvePR(
                    repo = repo,
                    prId = pr.id,
                    user = property.configuration.autoMergeUser,
                    token = property.configuration.autoMergeToken,
                )
                // Auto merge
                if (remoteAutoMerge) {
                    error("Server-side auto merge is not supported for Bitbucket Server")
                } else {
                    merged = waitAndMerge(prId, message)
                }
            }
            // PR
            return SCMPullRequest(
                id = pr.id.toString(),
                name = "PR-${pr.id}",
                link = "${property.configuration.url}/projects/${property.project}/repos/${property.repository}/pull-requests/${pr.id}/overview",
                merged = merged
            )
        }

        private fun waitAndMerge(prId: Int, message: String): Boolean {
            // Waits for the PR checks to be OK
            val autoApprovalTimeoutMillis = settings.autoMergeTimeout
            val autoApprovalIntervalMillis = settings.autoMergeInterval
            val merged = runBlocking {
                withTimeoutOrNull(timeMillis = autoApprovalTimeoutMillis) {
                    while (!client.isPRMergeable(repo, prId)) {
                        delay(autoApprovalIntervalMillis)
                    }
                    true // PR has become mergeable
                }
            }
            if (merged == null || !merged) {
                return false
            }
            // Merges the PR
            client.mergePR(
                repo,
                prId,
                message
            )
            // Merged
            return true
        }

        private fun checkProject(other: Project) {
            check(other.id == project.id) {
                "An SCM service can be used only for its project."
            }
        }

        private val client: BitbucketClient by lazy {
            bitbucketClientFactory.getBitbucketClient(property.configuration)
        }

    }
}