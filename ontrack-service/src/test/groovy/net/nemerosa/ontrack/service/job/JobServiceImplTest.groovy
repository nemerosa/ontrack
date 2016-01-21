package net.nemerosa.ontrack.service.job

import com.codahale.metrics.MetricRegistry
import net.nemerosa.ontrack.model.job.JobQueueAccessService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.service.security.SecurityServiceTestUtils
import org.junit.Before
import org.junit.Test
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.context.ApplicationContext

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.mock

class JobServiceImplTest {

    JobServiceImpl service

    @Before
    void before() {
        ApplicationContext applicationContext = mock(ApplicationContext)

        SecurityService securityService = SecurityServiceTestUtils.securityService()

        ApplicationLogService applicationLogService = mock(ApplicationLogService)
        JobQueueAccessService jobQueueAccessService = mock(JobQueueAccessService)
        CounterService counterService = mock(CounterService)
        MetricRegistry metricRegistry = new MetricRegistry()
        service = new JobServiceImpl(
                applicationContext,
                securityService,
                applicationLogService,
                jobQueueAccessService,
                counterService,
                metricRegistry
        )
    }

    @Test
    void 'Running job: disabled'() {
        TestJob job = TestJob.create(1, true)
        RegisteredJob rj = service.registerJob(0, job)
        assert !service.runJob(rj, false).present
        assert job.count == 0
    }

    @Test
    void 'Running job: group running'() {
        TestJob job = TestJob.create(1)
        TestJob jobLong = TestJob.create(2).longRunning()
        RegisteredJob rj = service.registerJob(0, job)
        RegisteredJob rjLong = service.registerJob(0, jobLong)
        assert service.runJob(rjLong, true).present
        Thread.sleep 500
        assert !service.runJob(rj, true).present
    }

    @Test
    void 'Running job: already running'() {
        TestJob jobLong = TestJob.create(2).longRunning()
        RegisteredJob rjLong = service.registerJob(0, jobLong)
        assert service.runJob(rjLong, true)
        Thread.sleep 500
        assert !service.runJob(rjLong, true).present
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
        assert service.runJob(rjLong, true).present
        Thread.sleep 500
        assert service.idInSameGroupRunning(rj)
    }

    @Test
    void 'Job run'() {
        def job = TestJob.create(1)
        def rj = service.registerJob(0, job)
        def o = service.runJob(rj, true)
        assert o != null
        assert o.present
        o.get().get(1, TimeUnit.SECONDS)
        assert job.count == 1
    }

    @Test
    void 'Cancel job'() {
        def job = TestJob.create(2).longRunning()
        def rj = service.registerJob(0, job)
        def o = service.runJob(rj, true)
        assert o != null
        assert o.present
        def future = o.get()
        assert !future.done
        assert !future.cancelled
        // Cancels the job
        future.cancel(true)
        // Checks
        assert future.done
        assert future.cancelled
        assert job.count == 0
    }

}