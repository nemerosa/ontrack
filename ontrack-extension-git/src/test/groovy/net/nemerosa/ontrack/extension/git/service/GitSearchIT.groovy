package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
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

import static net.nemerosa.ontrack.test.TestUtils.uid

class GitSearchIT extends AbstractServiceTestSupport {

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private GitService gitService

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
        } finally {
            repo.close()
        }
        // Issue service
        def issueServiceIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier().format()
        // Create a Git configuration
        String gitConfigurationName = uid('C')
        BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
            gitConfigurationService.newConfiguration(
                    BasicGitConfiguration.empty()
                            .withName(gitConfigurationName)
                            .withIssueServiceConfigurationIdentifier(issueServiceIdentifier)
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

        // Looks for an issue
        def info = asUser().with(project, ProjectView).call { gitService.getIssueInfo(branch.id, '2') }
        assert info != null
        assert info.issue.key == '2'
        assert info.issue.displayKey == '#2'
        assert info.commitInfos.size() == 1
        def ci = info.commitInfos.first()
        assert ci.branchInfos.size() == 1
        assert ci.branchInfos.first().branch.id == branch.id

    }

}
