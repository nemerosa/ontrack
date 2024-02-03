package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.export.ExportFormat
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogCommit
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.scm.service.SCMPath
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.of
import net.nemerosa.ontrack.model.support.MessageAnnotator
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile(value = [RunProfile.DEV, RunProfile.ACC, RunProfile.UNIT_TEST])
class MockSCMExtension(
    extensionFeature: SCMExtensionFeature,
    private val propertyService: PropertyService,
) : AbstractExtension(extensionFeature), SCMExtension {

    override fun getSCM(project: Project): SCM? =
        propertyService.getPropertyValue(project, MockSCMProjectPropertyType::class.java)?.let {
            MockSCM(it)
        }

    override val type: String = "mock"

    override fun getSCMPath(configName: String, ref: String): SCMPath? {
        TODO("Not yet implemented")
    }

    private val repositories = mutableMapOf<String, MockRepository>()

    fun repository(name: String) = repositories.getOrPut(name) {
        MockRepository(name)
    }

    data class MockPullRequest(
        val from: String,
        val to: String,
        val id: Int,
        val title: String,
        val approved: Boolean,
        val merged: Boolean,
        val reviewers: List<String>,
    )

    data class MockBranch(
        val name: String,
    )

    class MockRepository(
        val name: String,
    ) {

        private val issues = mutableMapOf<String, MockIssue>()

        private val commits = mutableMapOf<String, MutableList<MockCommit>>()

        private val files = mutableMapOf<String, MutableMap<String, String>>()
        private val createdBranches = mutableMapOf<String, String>()
        private val createdPullRequests = mutableListOf<MockPullRequest>()

        fun registerIssue(key: String, message: String) {
            issues[key] = MockIssue(name, key, message)
        }

        fun registerCommit(scmBranch: String, message: String): String {
            val list = commits.getOrPut(scmBranch) {
                mutableListOf()
            }
            val id = (list.size + 1).toString()
            list += MockCommit(
                repository = name,
                id = id,
                message = message,
            )
            return id
        }

        fun registerFile(scmBranch: String, path: String, content: String) {
            val branch = files.getOrPut(scmBranch) {
                mutableMapOf()
            }
            branch[path] = content
        }

        fun getFile(scmBranch: String?, path: String) = files[scmBranch ?: ""]?.get(path)

        fun createBranch(sourceBranch: String, newBranch: String): String {
            createdBranches[sourceBranch] = newBranch
            return newBranch
        }

        fun createPR(
            from: String,
            to: String,
            title: String,
            autoApproval: Boolean,
            remoteAutoMerge: Boolean,
            reviewers: List<String>,
        ): SCMPullRequest {
            val id = createdPullRequests.size + 1
            val pr = MockPullRequest(
                from = from,
                to = to,
                id = id,
                title = title,
                approved = autoApproval,
                merged = autoApproval, // Merged immediately?
                reviewers = reviewers,
            )

            if (autoApproval) {
                // Merging the PR means that the BASE branch (to) must be adapted
                // Gets the files from the HEAD
                files[from]?.forEach { (path, content) ->
                    registerFile(to, path, content)
                }
            }

            createdPullRequests += pr
            return SCMPullRequest(
                id = id.toString(),
                name = "#$id",
                link = "mock:pr:$id",
                merged = pr.merged,
            )
        }

        fun findPR(from: String?, to: String?): MockPullRequest? =
            createdPullRequests
                .filter { from == null || it.from == from }.firstOrNull { to == null || it.to == to }

        fun getBranch(name: String): MockBranch? = createdBranches[name]?.let {
            MockBranch(it)
        }

        fun findIssue(key: String) = issues[key]

        fun getCommits(fromCommit: String, toCommit: String): List<SCMChangeLogCommit> {
            val fromBranch = commits.entries.find { (_, commits) ->
                commits.any { it.id == fromCommit }
            }
            val toBranch = commits.entries.find { (_, commits) ->
                commits.any { it.id == toCommit }
            }

            if (fromBranch == null || toBranch == null) {
                return emptyList()
            } else if (fromBranch.key == toBranch.key) {
                val allCommits = fromBranch.value
                val indexFrom = allCommits.indexOfFirst { it.id == fromCommit }
                val indexTo = allCommits.indexOfFirst { it.id == toCommit }
                val lowIndex = minOf(indexFrom, indexTo)
                val highIndex = maxOf(indexFrom, indexTo)
                return if (lowIndex == highIndex) {
                    emptyList()
                } else {
                    allCommits.subList(
                        lowIndex + 1, // We don't want the first commit
                        highIndex + 1, // We want the last commit
                    ).reversed() // We want the commits from the most recent to the oldest
                }
            } else {
                TODO("The Mock SCM does not support yet getting a change log between two branches")
            }
        }

    }

    private inner class MockSCM(
        private val mockScmProjectProperty: MockSCMProjectProperty,
    ) : SCM, SCMChangeLogEnabled {

        override val type: String = "mock"
        override val engine: String = "mock"

        override val repositoryURI: String = "mock:${mockScmProjectProperty.name}"

        override val repositoryHtmlURL: String = ""

        override val repository: String = mockScmProjectProperty.name

        override fun getSCMBranch(branch: Branch): String? =
            propertyService.getPropertyValue(branch, MockSCMBranchPropertyType::class.java)?.name

        override fun createBranch(sourceBranch: String, newBranch: String): String =
            repository(mockScmProjectProperty.name).createBranch(sourceBranch, newBranch)

        override fun download(scmBranch: String?, path: String, retryOnNotFound: Boolean): ByteArray? =
            repository(mockScmProjectProperty.name).getFile(scmBranch, path)?.toByteArray()

        override fun upload(scmBranch: String, commit: String, path: String, content: ByteArray, message: String) {
            repository(mockScmProjectProperty.name).registerFile(scmBranch, path, content.decodeToString())
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
        ): SCMPullRequest = repository(mockScmProjectProperty.name).createPR(
            from = from,
            to = to,
            title = title,
            autoApproval = autoApproval,
            remoteAutoMerge = remoteAutoMerge,
            reviewers = reviewers,
        )

        override fun getDiffLink(commitFrom: String, commitTo: String): String? =
            "diff?from=$commitFrom&to=$commitTo"

        override fun getBuildCommit(build: Build): String? =
            propertyService.getPropertyValue(build, MockSCMBuildCommitPropertyType::class.java)?.id

        override suspend fun getCommits(fromCommit: String, toCommit: String): List<SCMChangeLogCommit> =
            repository(mockScmProjectProperty.name).getCommits(fromCommit, toCommit)

        override fun getConfiguredIssueService(): ConfiguredIssueService =
            ConfiguredIssueService(
                MockIssueServiceExtension(repository),
                MockIssueServiceConfiguration.INSTANCE,
            )
    }

    class MockIssueServiceConfiguration : IssueServiceConfiguration {
        override val serviceId: String = "mock"
        override val name: String = "mock"

        companion object {
            val INSTANCE = MockIssueServiceConfiguration()
        }
    }

    inner class MockIssueServiceExtension(
        private val repositoryName: String,
    ) : IssueServiceExtension {

        private val issuePattern = "([A-Z]+-\\d+)"
        private val issueRegex = issuePattern.toRegex()

        override fun getId(): String = "mock"

        override fun getName(): String = "Mock issues"

        override fun getConfigurationList(): List<IssueServiceConfiguration> = emptyList()

        override fun getConfigurationByName(name: String): IssueServiceConfiguration? = null

        override fun validIssueToken(token: String?): Boolean = true // Anything goes

        override fun extractIssueKeysFromMessage(
            issueServiceConfiguration: IssueServiceConfiguration,
            message: String,
        ): Set<String> =
            issueRegex.findAll(message).map { m ->
                m.groupValues[1]
            }.toSet()

        override fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration?): Optional<MessageAnnotator> {
            return Optional.of(
                RegexMessageAnnotator(issuePattern) { key: String? ->
                    of("a")
                        .attr("href", "mock://$name/issue/$key")
                        .text(key)
                }
            )
        }

        override fun getLinkForAllIssues(
            issueServiceConfiguration: IssueServiceConfiguration?,
            issues: MutableList<Issue>?
        ): String {
            TODO("Not yet implemented")
        }

        override fun getIssue(issueServiceConfiguration: IssueServiceConfiguration, issueKey: String): Issue? =
            repository(repositoryName).findIssue(issueKey)

        @Deprecated("Will be removed in V5. Deprecated in Java")
        override fun exportFormats(issueServiceConfiguration: IssueServiceConfiguration?): MutableList<ExportFormat> {
            TODO("Not yet implemented")
        }

        override fun exportIssues(
            issueServiceConfiguration: IssueServiceConfiguration?,
            issues: MutableList<out Issue>?,
            request: IssueChangeLogExportRequest?
        ): ExportedIssues {
            TODO("Not yet implemented")
        }

        override fun getIssueId(
            issueServiceConfiguration: IssueServiceConfiguration?,
            token: String?
        ): Optional<String> {
            TODO("Not yet implemented")
        }

        override fun getMessageRegex(issueServiceConfiguration: IssueServiceConfiguration?, issue: Issue?): String {
            TODO("Not yet implemented")
        }

        override val feature: ExtensionFeature
            get() = TODO("Not yet implemented")

    }
}