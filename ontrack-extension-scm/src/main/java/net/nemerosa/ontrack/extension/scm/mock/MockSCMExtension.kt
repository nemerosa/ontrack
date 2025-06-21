package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommit
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.scm.service.SCMPath
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.LegacyRegexMessageAnnotator
import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.of
import net.nemerosa.ontrack.model.support.MessageAnnotator
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
@Profile(RunProfile.DEV)
class MockSCMExtension(
    extensionFeature: SCMExtensionFeature,
    private val propertyService: PropertyService,
    private val structureService: StructureService,
    private val issueServiceRegistry: IssueServiceRegistry,
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
        val body: String,
        val approved: Boolean,
        val merged: Boolean,
        val reviewers: List<String>,
    )

    class MockRepository(
        val name: String,
    ) {

        private val revisionCount = AtomicLong(0)

        private val issues = mutableMapOf<String, MockIssue>()

        private val branches = mutableListOf<MockBranch>()

        private val files = mutableMapOf<String, MutableMap<String, String>>()
        private val createdBranches = mutableMapOf<String, String>()
        private val createdPullRequests = mutableListOf<MockPullRequest>()

        fun registerIssue(key: String, message: String, vararg types: String) {
            issues[key] = MockIssue(name, key, message, types = types.toSet())
        }

        /**
         * Registers a commit.
         *
         * @param scmBranch Branch where to generate the commit
         * @param message Commit message
         */
        fun registerCommit(scmBranch: String, message: String): String {
            val branch = branches.find { it.name == scmBranch } ?: branches.run {
                val newBranch = MockBranch(scmBranch)
                add(newBranch)
                newBranch
            }
            val indexOnBranch = (branch.commits.size + 1)
            val normalizedBranchName = scmBranch.replace(
                "[^a-zA-Z0-9\\.]".toRegex(),
                "-"
            )
            val prefix = "$normalizedBranchName-$indexOnBranch"
            val digest = DigestUtils.sha1Hex(prefix).take(7)
            val id = "$prefix-$digest"
            branch.commits += MockCommit(
                repository = name,
                revision = revisionCount.incrementAndGet(),
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

        fun deleteBranch(branch: String) {
            createdBranches.remove(branch)
        }

        fun createBranch(sourceBranch: String, newBranch: String): String {
            createdBranches[sourceBranch] = newBranch
            return newBranch
        }

        fun createPR(
            from: String,
            to: String,
            title: String,
            body: String,
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
                body = body,
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
                .filter {
                    branchMatches(it.from, from)
                }.firstOrNull {
                    branchMatches(it.to, to)
                }

        private fun branchMatches(
            actualBranch: String,
            branchPattern: String?
        ) = if (branchPattern == null) {
            true
        } else if (branchPattern.endsWith("*")) {
            actualBranch.startsWith(branchPattern.trimEnd('*'))
        } else {
            actualBranch == branchPattern
        }

        fun getBranch(namePattern: String): MockBranch? {
            val branch = if (namePattern.endsWith("*")) {
                createdBranches.entries.firstOrNull { (name, branch) ->
                    branchMatches(name, namePattern)
                }?.value
            } else {
                createdBranches[name]
            }
            return branch?.let { MockBranch(it) }
        }

        fun findIssue(key: String) = issues[key]

        fun getCommits(fromCommit: String, toCommit: String): List<SCMCommit> {
            val fromBranch = branches.find { (_, commits) ->
                commits.any { it.id == fromCommit }
            }
            val toBranch = branches.find { (_, commits) ->
                commits.any { it.id == toCommit }
            }

            if (fromBranch == null || toBranch == null) {
                return emptyList()
            } else if (fromBranch.name == toBranch.name) {
                val allCommits = fromBranch.commits
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
                /**
                 * WARNING: THE MOCK SCM IS NOT A SCM AND THE WAY THIS IS COMPUTED BELOW IS ONLY
                 *          SUITABLE FOR TESTS!!
                 */
                // Ordering the branches from the oldest to the newest
                val (oldestBranch, newestBranch) = listOf(fromBranch, toBranch).sortedBy { branch ->
                    branches.indexOfFirst { it.name == branch.name }
                }
                val oldestCommit: String
                val newestCommit: String
                if (oldestBranch.name == fromBranch.name) {
                    oldestCommit = fromCommit
                    newestCommit = toCommit
                } else {
                    oldestCommit = toCommit
                    newestCommit = fromCommit
                }
                // List of commits to gather
                val commits = mutableListOf<MockCommit>()
                // Index branches boundaries
                val oldestBranchIndex = branches.indexOfFirst { it.name == oldestBranch.name }
                val newestBranchIndex = branches.indexOfFirst { it.name == newestBranch.name }
                (oldestBranchIndex..newestBranchIndex).forEach { index ->
                    val branch = branches[index]
                    // If oldest branch, take commits only from the oldest commit (excluded)
                    if (branch.name == oldestBranch.name) {
                        var found = false
                        branch.commits.forEach { commit ->
                            if (found) {
                                commits += commit
                            } else {
                                found = commit.id == oldestCommit
                            }
                        }
                    }
                    // If newest branch, take commits only until the newest commit (included)
                    else if (branch.name == newestBranch.name) {
                        var found = false
                        branch.commits.forEach { commit ->
                            if (!found) {
                                commits += commit
                            }
                            found = commit.id == newestCommit
                        }
                    }
                    // If anything else, take all the commits
                    else {
                        commits += branch.commits
                    }
                }
                // OK
                return commits.reversed()
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

        override fun getBranchLastCommit(branch: String): String? =
            repository(mockScmProjectProperty.name).getBranch(branch)?.lastCommit?.id

        override fun deleteBranch(branch: String) {
            repository(mockScmProjectProperty.name).deleteBranch(branch)
        }

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
            body = description,
            autoApproval = autoApproval,
            remoteAutoMerge = remoteAutoMerge,
            reviewers = reviewers,
        )

        override fun getDiffLink(commitFrom: String, commitTo: String): String =
            "diff?from=$commitFrom&to=$commitTo"

        override fun getBuildCommit(build: Build): String? =
            propertyService.getPropertyValue(build, MockSCMBuildCommitPropertyType::class.java)?.id

        override suspend fun getCommits(fromCommit: String, toCommit: String): List<SCMCommit> =
            repository(mockScmProjectProperty.name).getCommits(fromCommit, toCommit)

        override fun getConfiguredIssueService(): ConfiguredIssueService? =
            if (mockScmProjectProperty.issueServiceIdentifier != null) {
                issueServiceRegistry.getConfiguredIssueService(mockScmProjectProperty.issueServiceIdentifier)
            } else {
                ConfiguredIssueService(
                    MockIssueServiceExtension(repository),
                    MockIssueServiceConfiguration.INSTANCE,
                )
            }

        override fun findBuildByCommit(project: Project, id: String): Build? =
            propertyService.findByEntityTypeAndSearchArguments(
                entityType = ProjectEntityType.BUILD,
                propertyType = MockSCMBuildCommitPropertyType::class,
                searchArguments = MockSCMBuildCommitProperty.getSearchArguments(id)
            ).map { buildId ->
                structureService.getBuild(buildId)
            }.firstOrNull { build ->
                build.project.id == project.id
            }
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

        private fun validIssueToken(token: String?): Boolean = true // Anything goes

        override fun extractIssueKeysFromMessage(
            issueServiceConfiguration: IssueServiceConfiguration,
            message: String,
        ): Set<String> =
            issueRegex.findAll(message).map { m ->
                m.groupValues[1]
            }.toSet()

        override fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration): MessageAnnotator {
            return LegacyRegexMessageAnnotator(issuePattern) { key: String? ->
                of("a")
                    .attr("href", "mock://$name/issue/$key")
                    .text(key)
            }
        }

        override fun getIssueTypes(
            issueServiceConfiguration: IssueServiceConfiguration,
            issue: Issue
        ): Set<String> = if (issue is MockIssue) {
            issue.types ?: emptySet()
        } else {
            emptySet()
        }

        override fun getIssue(issueServiceConfiguration: IssueServiceConfiguration, issueKey: String): Issue? =
            repository(repositoryName).findIssue(issueKey)

        override fun getIssueId(
            issueServiceConfiguration: IssueServiceConfiguration?,
            token: String?
        ): String? {
            TODO("Not yet implemented")
        }

        override fun getMessageRegex(issueServiceConfiguration: IssueServiceConfiguration?, issue: Issue?): String {
            TODO("Not yet implemented")
        }

        override val feature: ExtensionFeature
            get() = TODO("Not yet implemented")

    }
}