package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.model.job.JobQueueAccessService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.service.security.SecurityServiceTestUtils
import org.junit.Before
import org.junit.Test
import org.springframework.context.ApplicationContext

import static org.mockito.Mockito.mock

class JobServiceImplTest {

    JobServiceImpl service

    @Before
    void before() {
        ApplicationContext applicationContext = mock(ApplicationContext)

        SecurityService securityService = SecurityServiceTestUtils.securityService()

        ApplicationLogService applicationLogService = mock(ApplicationLogService)
        JobQueueAccessService jobQueueAccessService = mock(JobQueueAccessService)
        service = new JobServiceImpl(
                applicationContext,
                securityService,
                applicationLogService,
                jobQueueAccessService
        )
    }

    @Test
    void 'Running job: disabled'() {
        TestJob job = TestJob.create(1, true)
        RegisteredJob rj = service.registerJob(0, job)
        assert !service.runJob(rj, false)
        assert job.count == 0
    }

    @Test
    void 'Group run: no'() {
        TestJob job = TestJob.create(1)
        RegisteredJob rj = service.registerJob(0, job)
        assert !service.idInSameGroupRunning(rj)
    }

    @Test
    void 'Group run: yes'() {
        TestJob job = TestJob.create(1)
        TestJob jobLong = TestJob.create(2).longRunning()
        RegisteredJob rj = service.registerJob(0, job)
        RegisteredJob rjLong = service.registerJob(0, jobLong)
        assert service.runJob(rjLong, true)
        Thread.sleep 500
        assert service.idInSameGroupRunning(rj)
    }

}