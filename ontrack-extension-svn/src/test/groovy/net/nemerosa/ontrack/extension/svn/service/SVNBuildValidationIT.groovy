package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.api.model.BuildValidationException
import net.nemerosa.ontrack.extension.scm.support.TagPattern
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.support.*
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

/**
 * Integration tests for the search and collection of issues and revisions.
 */
@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SVNBuildValidationIT extends AbstractServiceTestSupport {

    @Autowired
    private PropertyService propertyService

    @Autowired
    private StructureService structureService

    @Autowired
    private SecurityService securityService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private TagNamePatternSvnRevisionLink tagNamePatternSvnRevisionLink;

    private SVNTestRepo repo

    @Before
    void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNBuildValidationIT')
    }

    @After
    void 'SVN repository: stop'() {
        repo.stop()
    }

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
        // SVN configuration
        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }
        // SVN branch configuration
        asUser().with(branch, ProjectEdit).call {
            // Project's configuration
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/project/trunk"
            ))
            // Branch's configuration
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/project/trunk",
                    new ConfiguredBuildSvnRevisionLink<>(
                            tagNamePatternSvnRevisionLink,
                            new TagPattern("1.1.*")
                    ).toServiceConfiguration()
            ))
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
        // Creates a branch
        def branch = doCreateBranch()
        // SVN configuration
        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }
        // SVN branch configuration
        asUser().with(branch, ProjectEdit).call {
            // Project's configuration
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/project/trunk"
            ))
            // Branch's configuration
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/project/trunk",
                    new ConfiguredBuildSvnRevisionLink<>(
                            tagNamePatternSvnRevisionLink,
                            new TagPattern("1.1.*")
                    ).toServiceConfiguration()
            ))
        }
        // Creates a build
        doCreateBuild(branch, nd('1.2.0', 'Build 1.2.0'))
    }

    /**
     * Validates a build according to the branch SCM policy.
     */
    @Test(expected = BuildValidationException)
    void 'Build validation not OK on rename'() {
        // SVN configuration
        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }
        // Creates a branch
        def branch = doCreateBranch()
        // Creates a build BEFORE the branch configuration
        def build = doCreateBuild(branch, nd('1.1.0', 'Build 1.1.0'))
        // SVN branch configuration
        asUser().with(branch, ProjectEdit).call {
            // Project's configuration
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/project/trunk"
            ))
            // Branch's configuration
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/project/trunk",
                    new ConfiguredBuildSvnRevisionLink<>(
                            tagNamePatternSvnRevisionLink,
                            new TagPattern("1.1.*")
                    ).toServiceConfiguration()
            ))
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
