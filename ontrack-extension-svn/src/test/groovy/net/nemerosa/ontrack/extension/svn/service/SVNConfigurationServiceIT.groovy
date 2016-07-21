package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.model.SVNURLFormatException
import net.nemerosa.ontrack.extension.svn.support.SVNProfileValueSource
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.ConnectionResult
import org.apache.commons.io.FileUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.annotation.ProfileValueSourceConfiguration

/**
 * SVN configuration service integration tests.
 */
@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SVNConfigurationServiceIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNConfigurationServiceIT')
    }

    @AfterClass
    static void 'SVN repository: stop'() {
        repo.stop()
    }

    @Autowired
    private IndexationService indexationService

    @Autowired
    private SVNConfigurationService configurationService

    @Autowired
    private SVNRepositoryDao repositoryDao

    @Autowired
    private SVNService svnService

    @Test(expected = SVNURLFormatException)
    void 'No trailing slash on the URL'() {
        configurationService.test(SVNConfiguration.of("test", "svn://localhost/")
                .withUser("test").withPassword("test"))
    }

    @Test
    @IfProfileValue(name = "svn", value = "true")
    void 'SVN configuration must point to the repository root - nok'() {
        repo.mkdir "SVNConfigurationSubFolder", "Sub folder"
        def result = configurationService.test(
                SVNConfiguration.of("test", "${repo.url}/SVNConfigurationSubFolder")
                        .withUser("test").withPassword("test")
        )
        assert result.type == ConnectionResult.ConnectionResultType.ERROR
        assert result.message == "${repo.url}/SVNConfigurationSubFolder must be the root of the repository." as String
    }

    @Test
    @IfProfileValue(name = "svn", value = "true")
    void 'SVN configuration must point to the repository root'() {
        def result = configurationService.test(
                SVNConfiguration.of("test", repo.url.toString())
                        .withUser("test").withPassword("test")
        )
        assert result.type == ConnectionResult.ConnectionResultType.OK
    }

    @Test
    @IfProfileValue(name = "svn", value = "true")
    void 'Repository deletion after configuration deletion'() {

        /**
         * Preparation of a SVN project with branch merged into the trunk
         */

        File wd = new File('build/work/SVNConfigurationServiceIT/RepositoryDeletion')
        FileUtils.forceMkdir(wd)
        // Few commits on the trunk
        repo.mkdir 'RepositoryDeletion/trunk', 'Trunk'
        (1..3).each { repo.mkdir "RepositoryDeletion/trunk/$it", "$it" }

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration

        // Saves the configuration
        asUser().with(GlobalSettings).call {
            configuration = configurationService.newConfiguration(configuration)
        }

        /**
         * Indexation of this repository
         */

        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(repositoryId, configuration, null)
        asUser().with(GlobalSettings).call {
            ((IndexationServiceImpl) indexationService).indexFromLatest(repository, { println it })
        }

        /**
         * Deletion of the configuration
         */
        asUser().with(GlobalSettings).call {
            configurationService.deleteConfiguration(configuration.name)
        }

        // Checks the repository has been deleted
        assert repositoryDao.findByName(configuration.name) == null: "The DB repository must be deleted"

    }

}
