package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.support.SVNProfileValueSource
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.annotation.ProfileValueSourceConfiguration

/**
 * Indexation service integration tests for the job scheduling.
 */
@ProfileValueSourceConfiguration(SVNProfileValueSource)
class IndexationServiceJobIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository: start'() {
        repo = SVNTestRepo.get('IndexationServiceJobIT')
    }

    @AfterClass
    static void 'SVN repository: stop'() {
        repo.stop()
    }

    @Autowired
    private SVNConfigurationService configurationService

    @Autowired
    private SVNRepositoryDao repositoryDao

    @Autowired
    private JobScheduler jobScheduler

    @Test
    @IfProfileValue(name = "svn", value = "true")
    void 'Scheduling and unscheduling'() {

        // Definition of the repository

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(repositoryId, configuration, null)


        // New configuration

        configuration = repository.configuration
        asUser().with(GlobalSettings).call {
            configurationService.newConfiguration(configuration)
        }

        // Checks the job is not scheduled and is registered

        Optional<JobStatus> optionalStatus = jobScheduler.getJobStatus(IndexationService.INDEXATION_JOB.getKey(configuration.name))
        assert optionalStatus.present
        assert optionalStatus.get().nextRunDate == null

        // Updating the configuration

        configuration = repository.configuration.withIndexationInterval(10)
        asUser().with(GlobalSettings).call {
            configurationService.updateConfiguration(configuration.name, configuration)
        }

        // Checks the job is scheduled and is registered

        optionalStatus = jobScheduler.getJobStatus(IndexationService.INDEXATION_JOB.getKey(configuration.name))
        assert optionalStatus.present
        assert optionalStatus.get().nextRunDate != null

        // Deletes the configuration

        asUser().with(GlobalSettings).call {
            configurationService.deleteConfiguration(configuration.name)
        }

        // Checks the job is not registered

        optionalStatus = jobScheduler.getJobStatus(IndexationService.INDEXATION_JOB.getKey(configuration.name))
        assert !optionalStatus.present


    }

}
