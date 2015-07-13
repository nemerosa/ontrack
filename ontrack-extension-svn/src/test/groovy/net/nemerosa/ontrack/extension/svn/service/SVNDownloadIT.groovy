package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.support.SVNProfileValueSource
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SVNDownloadIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNDownloadIT')
    }

    @AfterClass
    static void 'SVN repository: stop'() {
        repo.stop()
    }

    @Autowired
    private SVNService svnService

    @Autowired
    private IndexationService indexationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private IssueServiceRegistry issueServiceRegistry

    @Autowired
    private SVNRepositoryDao repositoryDao


    @Test
    void 'SVN Download'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'SVNDownload/trunk', 'Trunk'
        repo.file 'SVNDownload/trunk/folder/file1', 'Content 1', 'Commit file'
        repo.copy 'SVNDownload/trunk', 'SVNDownload/branches/v1', 'Branch 1'
        repo.file 'SVNDownload/trunk/folder/file1', 'Content 1', 'Commit file'

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository().configuration
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

        // Creates a project and branches
        Project project = doCreateProject()
        Branch branch1 = doCreateBranch(project, nd("branch1", ""))
        Branch trunk = doCreateBranch(project, nd("trunk", ""))

        // Configures the project
        asUser().with(project, ProjectEdit).call {
            propertyService.editProperty(
                    project,
                    SVNProjectConfigurationPropertyType,
                    new SVNProjectConfigurationProperty(configuration, '/SVNDownload/trunk')
            )
            // ...  & the branches
            propertyService.editProperty(
                    branch1,
                    SVNBranchConfigurationPropertyType,
                    new SVNBranchConfigurationProperty(
                            '/SVNDownload/branches/v1',
                            '/SVNDownload/tags/{build}',
                    )
            )
            propertyService.editProperty(
                    trunk,
                    SVNBranchConfigurationPropertyType,
                    new SVNBranchConfigurationProperty(
                            '/SVNDownload/trunk',
                            '/SVNDownload/tags/{build}',
                    )
            )
        }

        /**
         * Downloads the files for two different branches
         */

        asUser().with(project, ProjectConfig).call {
            assert svnService.download(branch1.id, 'folder/file1').get() == 'Content1'
            assert svnService.download(trunk.id, 'folder/file1').get() == 'Content2'
        }

    }
}
