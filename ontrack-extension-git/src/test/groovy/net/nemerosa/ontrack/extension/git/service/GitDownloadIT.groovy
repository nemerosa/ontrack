package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Integration tests for downloading a file
 */
class GitDownloadIT extends AbstractServiceTestSupport {

    @Autowired
    private GitService gitService

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    private GitRepo repo

    @Before
    void before() {
        repo = new GitRepo()
    }

    @Test
    void 'Download files'() {

        // Creates a Git repository with 10 commits and a branch
        repo.with {
            git 'init'
            file 'folder/file1', 'Content1'
            git 'commit', '-m', 'Commit 1'
            git 'checkout', '-b', 'branch1'
            git 'checkout', 'master'
            file 'folder/file1', 'Content2'
            git 'commit', '-m', 'Commit 2'
            git 'log', '--oneline', '--decorate', '--all'
        }

        // Identifies the commits
        def commits = [:]
        (1..2).each {
            commits[it as String] = repo.commitLookup("Commit $it")
        }

        // Create a Git configuration
        String gitConfigurationName = uid('C')
        BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
            gitConfigurationService.newConfiguration(
                    BasicGitConfiguration.empty()
                            .withName(gitConfigurationName)
                            .withRemote("file://${repo.dir.absolutePath}")
            )
        }

        // Creates a project and branches
        Project project = doCreateProject()
        Branch branch1 = doCreateBranch(project, nd("branch1", ""))
        Branch master = doCreateBranch(project, nd("master", ""))

        // Configures the project
        asUser().with(project, ProjectEdit).call {
            propertyService.editProperty(
                    project,
                    GitProjectConfigurationPropertyType,
                    new GitProjectConfigurationProperty(gitConfiguration)
            )
            // ...  & the branches
            [branch1, master].each {
                propertyService.editProperty(
                        it,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                it.name,
                                new ConfiguredBuildGitCommitLink<>(
                                        new CommitBuildNameGitCommitLink(),
                                        new CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }
        }

        // Downloads the files for two different branches
        asUser().with(project, ProjectConfig).call {
            assert gitService.download(branch1, 'folder/file1').get() == 'Content1'
            assert gitService.download(master, 'folder/file1').get() == 'Content2'
        }
    }

    @After
    void after() {
        repo.close()
    }
}
