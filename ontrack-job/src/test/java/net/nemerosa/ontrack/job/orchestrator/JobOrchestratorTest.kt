package net.nemerosa.ontrack.job.orchestrator;

import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.support.DefaultJobScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class JobOrchestratorTest {

    private ScheduledExecutorService scheduledExecutorService;

    @Before
    public void before() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @After
    public void after() {
        scheduledExecutorService.shutdownNow();
    }

    protected JobScheduler createJobScheduler() {
        return new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService, new OutputJobListener(
                System.out::println
        ), false, false, 1.0);
    }

    @Test
    public void orchestrator_initial_jobs() throws InterruptedException, ExecutionException {
        JobScheduler scheduler = createJobScheduler();

        Supplier<RuntimeException> notScheduledException = () -> new RuntimeException("Not scheduled");

        List<JobRegistration> jobs = new ArrayList<>();

        JobOrchestratorSupplier jobOrchestrationSupplier = jobs::stream;

        JobOrchestrator orchestrator = new JobOrchestrator(
                scheduler,
                "Test",
                Collections.singleton(jobOrchestrationSupplier)
        );
        JobKey key = orchestrator.getKey();

        // Orchestration is registered as a job, but does not run since we have a NONE schedule
        scheduler.schedule(orchestrator, Schedule.NONE);
        JobStatus status = scheduler.getJobStatus(key).orElse(null);
        assertNotNull(status);
        assertNull(status.getNextRunDate());

        // Puts a job in the list
        jobs.add(new JobRegistration(new TestJob("1"), Schedule.NONE));
        // ... and launches the orchestration
        scheduler.fireImmediately(key).orElseThrow(notScheduledException).get();
        // ... tests the job has been registered
        assertTrue(scheduler.getJobStatus(TestJob.getKey("1")).isPresent());

        // Puts the second job in the list
        jobs.add(new JobRegistration(new TestJob("2"), Schedule.NONE));
        // ... and launches the orchestration
        scheduler.fireImmediately(key).orElseThrow(notScheduledException).get();
        // ... tests the jobs are registered
        assertTrue(scheduler.getJobStatus(TestJob.getKey("1")).isPresent());
        assertTrue(scheduler.getJobStatus(TestJob.getKey("2")).isPresent());

        // Removes the first job in the list
        jobs.remove(0);
        // ... and launches the orchestration
        scheduler.fireImmediately(key).orElseThrow(notScheduledException).get();
        // ... tests the jobs are registered
        assertFalse(scheduler.getJobStatus(TestJob.getKey("1")).isPresent());
        assertTrue(scheduler.getJobStatus(TestJob.getKey("2")).isPresent());


    }

}
