package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.model.SVNLocation
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.service.IndexationService
import net.nemerosa.ontrack.extension.svn.service.IndexationServiceImpl
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

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

@ProfileValueSourceConfiguration(SVNProfileValueSource)
class RevisionSvnRevisionLinkIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository - start'() {
        repo = SVNTestRepo.get('RevisionSvnRevisionLinkIT')
    }

    @AfterClass
    static void 'SVN repository - stop'() {
        repo.stop()
    }

    @Autowired
    private SVNRepositoryDao repositoryDao

    @Autowired
    private IndexationService indexationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private RevisionSvnRevisionLink revisionLink

    @Test
    void 'Get earliest build using numeric build name'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'RevisionSvnRevisionLinkITEarliest/trunk', 'Trunk'
        (1..10).each { repo.mkdir "RevisionSvnRevisionLinkITEarliest/trunk/$it", "$it" }

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(repositoryId, configuration, null)

        /**
         * Saves the configuration
         */

        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }

        /**
         * Indexation of this repository
         */

        asUser().with(GlobalSettings).call {
            ((IndexationServiceImpl) indexationService).indexFromLatest(repository, { println it })
        }

        /**
         * Branch with this configuration
         */

        def branchConfigurationProperty = new SVNBranchConfigurationProperty(
                '/RevisionSvnRevisionLinkITEarliest/trunk',
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
         * Builds for some revisions
         */

        doCreateBuild(branch, nd("2", "Revision 2"))
        doCreateBuild(branch, nd("6", "Revision 6"))
        doCreateBuild(branch, nd("11", "Revision 11"))

        /**
         * Looks for some revisions
         */

        assert !revisionLink.getEarliestBuild(
                NoConfig.INSTANCE,
                branch,
                new SVNLocation("/RevisionSvnRevisionLinkITEarliest/branches/1.1", 7),
                null,
                branchConfigurationProperty
        ).present;

        assert revisionLink.getEarliestBuild(
                NoConfig.INSTANCE,
                branch,
                new SVNLocation("/RevisionSvnRevisionLinkITEarliest/trunk", 5),
                null,
                branchConfigurationProperty
        ).get().name == "6";

        assert revisionLink.getEarliestBuild(
                NoConfig.INSTANCE,
                branch,
                new SVNLocation("/RevisionSvnRevisionLinkITEarliest/trunk", 6),
                null,
                branchConfigurationProperty
        ).get().name == "6";

        assert revisionLink.getEarliestBuild(
                NoConfig.INSTANCE,
                branch,
                new SVNLocation("/RevisionSvnRevisionLinkITEarliest/trunk", 7),
                null,
                branchConfigurationProperty
        ).get().name == "11";

    }

}
