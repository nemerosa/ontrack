package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
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
 * Integration tests for Git as a SCM service
 */
class GitSCMServiceDetectorIT extends AbstractServiceTestSupport {

    @Autowired
    private SCMServiceDetector scmServiceDetector

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
    void 'Git as SCM service'() {

        // Creates a Git repository
        repo.with {
            git 'init'
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

        // Creates a project and branch
        Project project = doCreateProject()
        Branch branch = doCreateBranch(project, nd("branch", ""))

        // Configures the project
        asUser().with(project, ProjectEdit).call {
            propertyService.editProperty(
                    project,
                    GitProjectConfigurationPropertyType,
                    new GitProjectConfigurationProperty(gitConfiguration)
            )
            // ...  & the branches
            propertyService.editProperty(
                    branch,
                    GitBranchConfigurationPropertyType,
                    new GitBranchConfigurationProperty(
                            branch.name,
                            new ConfiguredBuildGitCommitLink<>(
                                    new CommitBuildNameGitCommitLink(),
                                    new CommitLinkConfig(true)
                            ).toServiceConfiguration(),
                            false, 0
                    )
            )
        }

        // Downloads the files for two different branches
        asUser().with(project, ProjectView).call {
            def service = scmServiceDetector.getScmService(branch)
            assert service.present: "Branch associated with a SCM service"

            def path = service.get().getSCMPathInfo(branch)
            assert path.present: "Branch associated with SCM path info"
            assert path.get().url == "file://${repo.dir.absolutePath}" as String
            assert path.get().branch == branch.name
            assert path.get().type == 'git'
        }
    }

    @After
    void after() {
        repo.close()
    }
}
