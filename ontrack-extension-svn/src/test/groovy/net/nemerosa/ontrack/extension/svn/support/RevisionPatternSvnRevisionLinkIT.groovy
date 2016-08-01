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
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

@ProfileValueSourceConfiguration(SVNProfileValueSource)
class RevisionPatternSvnRevisionLinkIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository - start'() {
        repo = SVNTestRepo.get('RevisionPatternSvnRevisionLinkIT')
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
    private RevisionPatternSvnRevisionLink revisionLink

    @Test
    void 'Get earliest build using numeric build name'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'RevisionPatternSvnRevisionLinkIT/trunk', 'Trunk'
        (1..10).each { repo.mkdir "RevisionPatternSvnRevisionLinkIT/trunk/$it", "$it" }

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

        def revisionPattern = new RevisionPattern('11.8.4.*-{revision}')
        def branchConfigurationProperty = new SVNBranchConfigurationProperty(
                '/RevisionPatternSvnRevisionLinkIT/trunk',
                new ConfiguredBuildSvnRevisionLink<>(
                        revisionLink,
                        revisionPattern
                ).toServiceConfiguration()
        )

        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    '/RevisionPatternSvnRevisionLinkIT/trunk'
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, branchConfigurationProperty)
        }

        /**
         * Builds for some revisions
         */

        doCreateBuild(branch, nd("11.8.4.0-2", "Revision 2"))
        doCreateBuild(branch, nd("11.8.4.0-6", "Revision 6"))
        doCreateBuild(branch, nd("11.8.4.1-11", "Revision 11"))

        /**
         * Looks for some revisions
         */

        asUser().withView(branch).call {

            assert !revisionLink.getEarliestBuild(
                    revisionPattern,
                    branch,
                    new SVNLocation("/RevisionPatternSvnRevisionLinkIT/branches/11.8.5", 7),
                    null,
                    branchConfigurationProperty
            ).present;

            assert revisionLink.getEarliestBuild(
                    revisionPattern,
                    branch,
                    new SVNLocation("/RevisionPatternSvnRevisionLinkIT/trunk", 5),
                    null,
                    branchConfigurationProperty
            ).get().name == "11.8.4.0-6";

            assert revisionLink.getEarliestBuild(
                    revisionPattern,
                    branch,
                    new SVNLocation("/RevisionPatternSvnRevisionLinkIT/trunk", 6),
                    null,
                    branchConfigurationProperty
            ).get().name == "11.8.4.0-6";

            assert revisionLink.getEarliestBuild(
                    revisionPattern,
                    branch,
                    new SVNLocation("/RevisionPatternSvnRevisionLinkIT/trunk", 7),
                    null,
                    branchConfigurationProperty
            ).get().name == "11.8.4.1-11";
        }

    }

}
