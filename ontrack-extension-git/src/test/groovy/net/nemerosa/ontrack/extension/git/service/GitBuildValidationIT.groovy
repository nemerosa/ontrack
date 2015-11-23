package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.model.BuildValidationException
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.TagPatternBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.scm.support.TagPattern
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Tests for #187 - validation of the build name
 */
@ActiveProfiles(profiles = [RunProfile.UNIT_TEST, "git.mock"])
class GitBuildValidationIT extends AbstractServiceTestSupport {

    @Autowired
    private PropertyService propertyService

    @Autowired
    private StructureService structureService

    @Autowired
    private SecurityService securityService

    @Autowired
    private GitConfigurationService gitConfigurationService

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test
    void 'Build validation OK without branch config'() {
        // Creates a branch
        def branch = doCreateBranch()
        // Creates a build
        doCreateBuild(branch, nd('1', 'Build 1'))
        // Checks the build has been created
        def build = asUser().with(branch, ProjectView).call {
            structureService.findBuildByName(branch.project.name, branch.name, '1')
        }
        assert build.present
        assert build.get().name == '1'
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test
    void 'Build validation OK with tag pattern'() {
        // Creates a branch
        def branch = doCreateBranch()
        // Git branch configuration
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch,
                    GitBranchConfigurationPropertyType,
                    new GitBranchConfigurationProperty(
                            'master',
                            new ConfiguredBuildGitCommitLink<>(
                                    new TagPatternBuildNameGitCommitLink(),
                                    new TagPattern("1.1.*")
                            ).toServiceConfiguration(),
                            false,
                            0
                    )
            )
        }
        // Creates a build
        doCreateBuild(branch, nd('1.1.0', 'Build 1.1.0'))
        // Checks the build has been created
        def build = asUser().with(branch, ProjectView).call {
            structureService.findBuildByName(branch.project.name, branch.name, '1.1.0')
        }
        assert build.present
        assert build.get().name == '1.1.0'
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test(expected = BuildValidationException)
    void 'Build validation not OK with tag pattern'() {
        // Git configuration
        String gitConfigurationName = uid('C')
        BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
            gitConfigurationService.newConfiguration(
                    BasicGitConfiguration.empty()
                            .withName(gitConfigurationName)
                            .withRemote("not.used")
            )
        }
        // Creates a branch
        def branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            // Project configuration
            propertyService.editProperty(
                    branch.project,
                    GitProjectConfigurationPropertyType,
                    new GitProjectConfigurationProperty(gitConfiguration)
            )
            // Git branch configuration
            propertyService.editProperty(
                    branch,
                    GitBranchConfigurationPropertyType,
                    new GitBranchConfigurationProperty(
                            'master',
                            new ConfiguredBuildGitCommitLink<>(
                                    new TagPatternBuildNameGitCommitLink(),
                                    new TagPattern("1.1.*")
                            ).toServiceConfiguration(),
                            false,
                            0
                    )
            )
        }
        // Creates a build
        doCreateBuild(branch, nd('1.2.0', 'Build 1.2.0'))
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test(expected = BuildValidationException)
    void 'Build validation not OK on rename'() {
        // Git configuration
        String gitConfigurationName = uid('C')
        BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
            gitConfigurationService.newConfiguration(
                    BasicGitConfiguration.empty()
                            .withName(gitConfigurationName)
                            .withRemote("not.used")
            )
        }
        // Creates a branch
        def branch = doCreateBranch()
        // Creates a build BEFORE the branch configuration
        def build = doCreateBuild(branch, nd('1.1.0', 'Build 1.1.0'))
        asUser().with(branch, ProjectEdit).call {
            // Project configuration
            propertyService.editProperty(
                    branch.project,
                    GitProjectConfigurationPropertyType,
                    new GitProjectConfigurationProperty(gitConfiguration)
            )
            // Git branch configuration
            propertyService.editProperty(
                    branch,
                    GitBranchConfigurationPropertyType,
                    new GitBranchConfigurationProperty(
                            'master',
                            new ConfiguredBuildGitCommitLink<>(
                                    new TagPatternBuildNameGitCommitLink(),
                                    new TagPattern("1.1.*")
                            ).toServiceConfiguration(),
                            false,
                            0
                    )
            )
        }
        // Renames the build
        asUser().with(branch, ProjectEdit).call {
            structureService.saveBuild(
                    Build.of(
                            branch,
                            nd('1.2.0', 'New build'),
                            securityService.currentSignature
                    ).withId(build.id)
            )
        }
    }
}
