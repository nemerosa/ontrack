package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.support.NoConfig
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SvnSCMServiceDetectorIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository - start'() {
        repo = SVNTestRepo.get('SvnSCMServiceDetectorIT')
    }

    @AfterClass
    static void 'SVN repository - stop'() {
        repo.stop()
    }

    @Autowired
    private SVNRepositoryDao repositoryDao

    @Autowired
    private SCMServiceDetector scmServiceDetector

    @Autowired
    private PropertyService propertyService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private RevisionSvnRevisionLink revisionLink

    @Test
    void 'SVN as SCM service'() {

        /**
         * Preparation of a repository
         */

        repo.mkdir 'SVNAsSCM/trunk', 'Trunk'

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration

        /**
         * Saves the configuration
         */

        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }

        /**
         * Branch with this configuration
         */

        def branchConfigurationProperty = new SVNBranchConfigurationProperty(
                '/SVNAsSCM/trunk',
                new ConfiguredBuildSvnRevisionLink<>(
                        revisionLink,
                        NoConfig.INSTANCE
                ).toServiceConfiguration()
        )

        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    '/RevisionSvnRevisionLinkITEarliest/trunk'
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, branchConfigurationProperty)
        }

        /**
         * Gets the SVN service
         */


        def scmService = scmServiceDetector.getScmService(branch)
        assert scmService.present: "The branch has a SCM service"

        // Path to the branch
        def path = scmService.get().getSCMPathInfo(branch)
        assert path.present: "The branch has a SCM path info"
        assert path.get().url == "${repo.url}/SVNAsSCM/trunk" as String

    }

}
