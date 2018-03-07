package net.nemerosa.ontrack.job.support;

import com.google.common.collect.ImmutableSet;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

public class DefaultJobSchedulerTest {

    private SynchronousScheduledExecutorService schedulerPool;
    private SynchronousScheduledExecutorService jobPool;

    private final Supplier<RuntimeException> noFutureException = () -> new IllegalStateException("No future being returned.");

    @Before
    public void before() {
        schedulerPool = new SynchronousScheduledExecutorService();
        jobPool = new SynchronousScheduledExecutorService();
    }

    @After
    public void after() {
        schedulerPool.shutdownNow();
        jobPool.shutdownNow();
    }

    protected JobScheduler createJobScheduler() {
        return createJobScheduler(false);
    }

    protected JobScheduler createJobScheduler(boolean initiallyPaused) {
        return new DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                schedulerPool,
                NOPJobListener.INSTANCE,
                initiallyPaused,
                (pool, job) -> jobPool,
                false,
                1.0
        );
    }

    @Test
    public void scheduler_paused_at_startup_with_orchestration() throws InterruptedException {
        // Creates a job scheduler
        JobScheduler jobScheduler = createJobScheduler(true);
        // Job orchestration
        TestJob job = TestJob.of();
        JobOrchestrator jobOrchestrator = new JobOrchestrator(
                jobScheduler,
                "Orchestrator",
                Collections.singletonList(
                        () -> Stream.of(
                                JobRegistration.of(job).withSchedule(Schedule.EVERY_SECOND)
                        )
                )
        );
        // Registers the orchestrator
        jobScheduler.schedule(jobOrchestrator, Schedule.EVERY_SECOND);
        // Waits some time...
        tick_seconds(3);
        // ... and the job should not have run
        assertEquals(0, job.getCount());
        // ... and the orchestrator must not have run
        Optional<JobStatus> orchestratorStatus = jobScheduler.getJobStatus(jobOrchestrator.getKey());
        assertTrue(
                orchestratorStatus.isPresent() &&
                        orchestratorStatus.get().getRunCount() == 0 &&
                        !orchestratorStatus.get().isRunning()
        );
        // Resumes the job scheduler
        jobScheduler.resume();
        // Resumes all jobs
        schedulerPool.runUntilIdle();
        // Waits for one second for the orchestrator to kick off
        tick_seconds(1);
        // Forces the registration of pending jobs
        schedulerPool.runUntilIdle();
        jobPool.runUntilIdle();
        // The job managed by the orchestrator must have run
        assertEquals(1, job.getCount());
    }

    /**
     * Runs a piece of codes a given number of times
     *
     * @param count Number of times to run
     * @param code  Code to run
     */
    public static void times(int count, Runnable code) {
        for (int i = 0; i < count; i++) {
            code.run();
        }
    }

    /**
     * Runs the scheduler for x seconds with intervals of 1/2 seconds.
     */
    protected void tick_seconds(int count) {
        times(count * 2, () -> {
            schedulerPool.tick(500, MILLISECONDS);
            jobPool.runUntilIdle();
        });
    }

}
