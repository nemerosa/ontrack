package net.nemerosa.ontrack.extension.stash.scm

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.scm.service.SCMPath
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.stash.StashExtensionFeature
import net.nemerosa.ontrack.extension.stash.client.BitbucketClient
import net.nemerosa.ontrack.extension.stash.client.BitbucketClientFactory
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.model.getRepositoryUrl
import net.nemerosa.ontrack.extension.stash.property.StashGitConfiguration
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
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
    private val bitbucketClientFactory: BitbucketClientFactory,
    private val cachedSettingsService: CachedSettingsService,
    private val stashConfigurationService: StashConfigurationService,
) : AbstractExtension(extensionFeature), SCMExtension {

    override val type: String = "bitbucket-server"

    override fun getSCMPath(configName: String, ref: String): SCMPath? {
        val config = stashConfigurationService.findConfiguration(configName)
            ?: return null
        val regex = "([^\\/]*)\\/([^\\/]*)\\/(.*)\$".toRegex()
        val m = regex.matchEntire(ref)
            ?: throw BitbucketServerSCMRefParsingException("Cannot get the project and repository out of the reference: $ref")
        val (_, project, repository, path) = m.groupValues
        val scm = BitbucketServerSCM(
            configuration = config,
            project = project,
            repositoryName = repository,
            settings = cachedSettingsService.getCachedSettings(BitbucketServerSettings::class.java)
        )
        return SCMPath(
            scm = scm,
            path = path,
        )
    }

    override fun getSCM(project: Project): SCM? {
        val property =
            propertyService.getPropertyValue(project, StashProjectConfigurationPropertyType::class.java) ?: return null
        return BitbucketServerSCM(
            configuration = property.configuration,
            project = property.project,
            repositoryName = property.repository,
            settings = cachedSettingsService.getCachedSettings(BitbucketServerSettings::class.java),
        )
    }

    private inner class BitbucketServerSCM(
        private val configuration: StashConfiguration,
        private val project: String,
        private val repositoryName: String,
        private val settings: BitbucketServerSettings,
    ) : SCM {

        override val type: String = "git"
        override val engine: String = "bitbucket-server"

        private val stashConfiguration = StashGitConfiguration(
            configuration = configuration,
            project = project,
            repository = repositoryName,
            indexationInterval = 0, // Not needed in this context
            configuredIssueService = null, // Not needed in this context
        )

        private val repo = BitbucketRepository(project, repositoryName)

        override val repositoryURI: String = stashConfiguration.remote

        override val repositoryHtmlURL: String = getRepositoryUrl(configuration, project, repositoryName)

        override val repository: String = "${project}/${repositoryName}"

        override fun getSCMBranch(branch: Branch): String? {
            val branchProperty: GitBranchConfigurationProperty? =
                propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
            return branchProperty?.branch
        }

        override fun createBranch(sourceBranch: String, newBranch: String): String =
            client.createBranch(repo, sourceBranch, newBranch)

        override fun download(scmBranch: String?, path: String, retryOnNotFound: Boolean): ByteArray? =
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
            message: String,
            reviewers: List<String>,
        ): SCMPullRequest {
            // Creates the pull request
            val pr = client.createPR(
                repo = repo,
                title = title,
                head = from,
                base = to,
                body = description,
                reviewers = reviewers,
            )
            val prId = pr.id
            // Auto approval process (approval + wait for checks + merge)
            var merged = false
            // Auto approval
            if (autoApproval) {
                // Auto merge token must be set
                if (configuration.autoMergeToken.isNullOrBlank()) {
                    throw BitbucketServerSCMMissingAutoMergeTokenException(configuration.name)
                }
                if (configuration.autoMergeUser.isNullOrBlank()) {
                    throw BitbucketServerSCMMissingAutoMergeUserException(configuration.name)
                }
                // Approving using the auto merge account
                client.approvePR(
                    repo = repo,
                    prId = pr.id,
                    user = configuration.autoMergeUser,
                    token = configuration.autoMergeToken,
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
                link = "${configuration.url}/projects/${project}/repos/${repositoryName}/pull-requests/${pr.id}/overview",
                merged = merged
            )
        }

        override fun getDiffLink(commitFrom: String, commitTo: String): String {
            return "${configuration.url}/projects/${project}/repos/${repositoryName}/compare/commits?targetBranch=${commitTo}&sourceBranch=${commitFrom}"
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

        private val client: BitbucketClient by lazy {
            bitbucketClientFactory.getBitbucketClient(configuration)
        }

    }
}