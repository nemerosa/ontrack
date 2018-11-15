package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
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
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

class GitSearchIT extends AbstractServiceTestSupport {

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private GitService gitService

    @Autowired
    private GitRepositoryClientFactory gitRepositoryClientFactory

    @Autowired
    private MockIssueServiceExtension mockIssueServiceExtension

    @Test
    void 'Issue search on one branch'() {
        // Git repository
        def repo = new GitRepo()
        try {
            repo.with {
                git 'init'
                commit 1, '#1'
                commit 2, '#2'
                commit 3, '#2'
                commit 4, '#1'
                git 'log', '--oneline', '--decorate'
            }

            // Create a Git configuration
            String gitConfigurationName = uid('C')
            BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a project and branch
            Branch branch = doCreateBranch()
            Project project = branch.project

            // Configures the project
            asUser().with(project, ProjectEdit).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType,
                        new GitProjectConfigurationProperty(gitConfiguration)
                )
                // ...  & the branch with a link based on commits
                propertyService.editProperty(
                        branch,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                'master',
                                new ConfiguredBuildGitCommitLink<>(
                                        new CommitBuildNameGitCommitLink(),
                                        new CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Need to force the Git sync first
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).sync { println it }

            // Looks for an issue
            def info = asUser().with(project, ProjectView).call { gitService.getIssueInfo(branch.id, '2') }
            assert info != null
            assert info.issue.key == '2'
            assert info.issue.displayKey == '#2'
            assert info.commitInfos.size() == 1
            def ci = info.commitInfos.first()
            assert ci.branchInfos.size() == 1
            assert ci.branchInfos.first().branch.id == branch.id

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
    @Test
    void 'Issue search with links between two branches'() {
        // Git repository
        def repo = new GitRepo()
        try {
            repo.with {
                git 'init'
                commit 1, '#1'
                git 'checkout', '-b', '1.0'
                commit 2, '#2'
                git 'checkout', 'master'
                commit 3, '#3'
                git 'log', '--oneline', '--decorate'
            }

            // Create a Git configuration
            String gitConfigurationName = uid('C')
            BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a project and two branches
            Project project = doCreateProject()
            Branch master = doCreateBranch(project, nd('master', 'Master branch'))
            Branch branch10 = doCreateBranch(project, nd('1.0', '1.0 branch'))

            // Configures the project
            asUser().with(project, ProjectEdit).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType,
                        new GitProjectConfigurationProperty(gitConfiguration)
                )
                // ...  & the branches with a link based on commits
                propertyService.editProperty(
                        master,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                'master',
                                new ConfiguredBuildGitCommitLink<>(
                                        new CommitBuildNameGitCommitLink(),
                                        new CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
                propertyService.editProperty(
                        branch10,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                '1.0',
                                new ConfiguredBuildGitCommitLink<>(
                                        new CommitBuildNameGitCommitLink(),
                                        new CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Links between issues
            def issue1 = new MockIssue(1, MockIssueStatus.OPEN, 'feature')
            def issue2 = new MockIssue(2, MockIssueStatus.OPEN, 'feature')

            issue2.withLinks([issue1])
            issue1.withLinks([issue2])

            mockIssueServiceExtension.register(issue1, issue2)

            // Need to force the Git sync first
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).sync { println it }

            // Looks for an issue
            def info = asUser().with(project, ProjectView).call { gitService.getIssueInfo(master.id, '2') }
            assert info != null
            assert info.issue.key == '2'
            assert info.issue.displayKey == '#2'
            assert info.commitInfos.size() == 2
            assert info.commitInfos.first().branchInfos.size() == 1
            assert info.commitInfos.first().branchInfos.first().branch.id == branch10.id
            assert info.commitInfos.last().branchInfos.size() == 1
            assert info.commitInfos.last().branchInfos.first().branch.id == master.id

        } finally {
            repo.close()
        }

    }

}
