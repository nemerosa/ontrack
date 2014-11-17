package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.client.impl.GitTestUtils
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Integration tests for Git support.
 */
class GitIT extends AbstractServiceTestSupport {

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    @Test
    void 'Change log based on commits'() {

        def repo = new GitTestUtils()
        try {

            // Creates a Git repository with 10 commits
            repo.with {
                run 'git', 'init'
                (1..10).each { commit it }
                run 'git', 'log', '--oneline'
            }

            // Identifies the commits
            def commits = [:]
            (1..10).each {
                commits[it] = repo.commitLookup("Commit $it")
            }

            // Create a Git configuration
            String gitConfigurationName = uid('C')
            GitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
                gitConfigurationService.newConfiguration(
                        GitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withRemote("file://${repo.dir.absolutePath}")
                                .withBuildCommitLink(null)
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
                                        new CommitLinkConfig(false)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Creates builds for some commits
            asUser().with(project, ProjectEdit).call {
                [2, 5, 7, 8].each {
                    sleep 100 // Some delay to get correct timestamps in builds
                    structureService.newBuild(
                            Build.of(
                                    branch,
                                    NameDescription.nd(commits[it] as String, "Build $it"),
                                    Signature.of('test')
                            )
                    )
                }
            }

        } finally {
            repo.close()
        }

    }

}
