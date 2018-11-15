package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.git.GitRepositoryClientFactory

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NoConfig
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

class GitCommitPropertyCommitLinkIT extends AbstractServiceTestSupport {

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private GitRepositoryClientFactory gitRepositoryClientFactory

    @Autowired
    private GitService gitService

    @Autowired
    private GitCommitPropertyCommitLink gitCommitPropertyCommitLink

    @Test
    void 'Issue search on one branch'() {
        // Git repository
        def repo = new GitRepo()
        try {

            // Some commits
            repo.with {
                git 'init'
                (1..10).each {
                    commit it, "Commit $it"
                }
                git 'log', '--oneline', '--decorate'
            }

            // Identifies the commits
            def commits = [:]
            (1..10).each {
                commits[it] = repo.commitLookup("Commit $it", false)
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

            // Need to force the Git sync first
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).sync { println it }

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
                // ...  & the branch with a link based on property
                propertyService.editProperty(
                        branch,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                'master',
                                new ConfiguredBuildGitCommitLink<>(
                                        gitCommitPropertyCommitLink,
                                        NoConfig.INSTANCE
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
                // ... & creates some builds associated with some commits
                ['1.0': 3, '1.1': 6].each { name, int commitIndex ->
                    def build = structureService.newBuild(
                            Build.of(
                                    branch,
                                    NameDescription.nd(name, "Build commit number $commitIndex"),
                                    Signature.of('test')
                            )
                    )
                    propertyService.editProperty(
                            build,
                            GitCommitPropertyType,
                            new GitCommitProperty(commits[commitIndex] as String)
                    )
                }
            }

            // Commit info

            def commitInfo = asUser().with(project, ProjectView).call {
                gitService.getCommitInfo(branch.id, commits[4] as String)
            }
            assert commitInfo != null
            assert commitInfo.uiCommit.commit.id == commits[4] as String
            assert commitInfo.uiCommit.annotatedMessage == "Commit 4"
            assert commitInfo.buildViews[0].build.description == "Build commit number 6"
        } finally {
            repo.close()
        }

    }

}
