package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.model.job.JobQueueAccessService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.junit.Before
import org.junit.Test
import org.springframework.context.ApplicationContext

import static org.mockito.Mockito.mock

class JobServiceImplTest {

    JobServiceImpl service

    @Before
    void before() {
        ApplicationContext applicationContext = mock(ApplicationContext)
        SecurityService securityService = mock(SecurityService)
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

}