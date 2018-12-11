package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.OntrackGitIssueInfo
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.extension.issues.support.MockIssue
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceExtension
import net.nemerosa.ontrack.extension.issues.support.MockIssueStatus
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitSearchIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var gitConfigurationService: GitConfigurationService

    @Autowired
    private lateinit var gitService: GitService

    @Autowired
    private lateinit var gitRepositoryClientFactory: GitRepositoryClientFactory

    @Autowired
    private lateinit var mockIssueServiceExtension: MockIssueServiceExtension

    @Autowired
    private lateinit var commitBuildNameGitCommitLink: CommitBuildNameGitCommitLink

    @Test
    fun `Issue search on one branch`() {
        // Git repository
        val repo = GitRepo()
        try {
            repo.apply {
                gitInit()
                commit(1, "#1")
                commit(2, "#2")
                commit(3, "#2")
                commit(4, "#1")
                log()
            }

            // Create a Git configuration
            val gitConfigurationName = uid("C")
            val gitConfiguration = asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a project and branch
            val branch = doCreateBranch()
            val project = branch.project

            // Configures the project
            asUser().with(project, ProjectEdit::class.java).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType::class.java,
                        GitProjectConfigurationProperty(gitConfiguration)
                )
                // ...  & the branch with a link based on commits
                propertyService.editProperty(
                        branch,
                        GitBranchConfigurationPropertyType::class.java,
                        GitBranchConfigurationProperty(
                                "master",
                                ConfiguredBuildGitCommitLink(
                                        commitBuildNameGitCommitLink,
                                        CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Need to force the Git sync first
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).sync(Consumer { println(it) })

            // Looks for an issue
            val info: OntrackGitIssueInfo? = asUserWithView(project).call { gitService.getIssueInfo(branch.id, "2") }
            assertNotNull(info) {
                assertEquals("2", it.issue.key)
                assertEquals("#2", it.issue.displayKey)
                val commitInfos = it.commitInfos
                assertEquals(1, commitInfos.size)
                val ci = commitInfos.first()
                val branchInfos = ci.branchInfos
                assertEquals(1, branchInfos.size)
                assertEquals(branch.id(), branchInfos.first().branch.id())
            }

        } finally {
            repo.close()
        }

    }

    /**
     * Gets issue info across two branches by following issue links.
     *
     * <pre>
     *     |  |
     *     *  | Commit #3 (master)
     *     |  |
     *     |  * Commit #2 (1.0)
     *     |  |
     *     |  /
     *     | /
     *     * Commit #1
     * </pre>
     *
     * If issues #3 and #2 are linked, lLooking for issue #3 must bring two branches:
     * trunk with revision 5, and branch 1.0 with revision 4
     */
    // FIXME #625 This test must be fixed (looking for issues)
    @Test
    @Ignore
    fun `Issue search with links between two branches`() {
        // Git repository
        val repo = GitRepo()
        try {
            repo.apply {
                gitInit()
                commit(1, "#1")
                git("checkout", "-b", "1.0")
                commit(2, "#2")
                git("checkout", "master")
                commit(3, "#3")
                log()
            }

            // Create a Git configuration
            val gitConfigurationName = uid("C")
            val gitConfiguration = asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a project and two branches
            val project = doCreateProject()
            val master = doCreateBranch(project, nd("master", "Master branch"))
            val branch10 = doCreateBranch(project, nd("1.0", "1.0 branch"))

            // Configures the project
            asUser().with(project, ProjectEdit::class.java).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType::class.java,
                        GitProjectConfigurationProperty(gitConfiguration)
                )
                // ...  & the branches with a link based on commits
                propertyService.editProperty(
                        master,
                        GitBranchConfigurationPropertyType::class.java,
                        GitBranchConfigurationProperty(
                                "master",
                                ConfiguredBuildGitCommitLink(
                                        commitBuildNameGitCommitLink,
                                        CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
                propertyService.editProperty(
                        branch10,
                        GitBranchConfigurationPropertyType::class.java,
                        GitBranchConfigurationProperty(
                                "1.0",
                                ConfiguredBuildGitCommitLink(
                                        commitBuildNameGitCommitLink,
                                        CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Links between issues
            val issue1 = MockIssue(1, MockIssueStatus.OPEN, "feature")
            val issue2 = MockIssue(2, MockIssueStatus.OPEN, "feature")

            issue2.withLinks(listOf(issue1))
            issue1.withLinks(listOf(issue2))

            mockIssueServiceExtension.register(issue1, issue2)

            // Need to force the Git sync first
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).sync(Consumer { println(it) })

            // Looks for an issue
            val info: OntrackGitIssueInfo? = asUserWithView(project).call { gitService.getIssueInfo(master.id, "2") }

            assertNotNull(info) {
                assertEquals("2", it.issue.key)
                assertEquals("#2", it.issue.displayKey)
                val commitInfos = it.commitInfos
                assertEquals(2, commitInfos.size)

                assertEquals(listOf(branch10.id()), commitInfos[0].branchInfos.map { it.branch.id() })
                assertEquals(listOf(master.id()), commitInfos[1].branchInfos.map { it.branch.id() })
            }

        } finally {
            repo.close()
        }

    }

}
