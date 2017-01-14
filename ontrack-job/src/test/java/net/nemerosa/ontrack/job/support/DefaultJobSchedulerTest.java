package net.nemerosa.ontrack.job.support;

import com.google.common.collect.ImmutableSet;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class DefaultJobSchedulerTest {

    private ScheduledExecutorService scheduledExecutorService;

    private final Supplier<RuntimeException> noFutureException = () -> new IllegalStateException("No future being returned.");

    @Before
    public void before() {
        scheduledExecutorService = Executors.newScheduledThreadPool(
                5,
                new BasicThreadFactory.Builder()
                        .daemon(true)
                        .namingPattern("test-job-%s")
                        .build()

        );
    }

    @After
    public void after() {
        scheduledExecutorService.shutdownNow();
    }

    protected JobScheduler createJobScheduler() {
        return createJobScheduler(false);
    }

    protected JobScheduler createJobScheduler(boolean initiallyPaused) {
        return new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService, NOPJobListener.INSTANCE, initiallyPaused);
    }

    @Test
    public void schedule() throws InterruptedException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        Thread.sleep(3000);
        assertTrue(job.getCount() >= 2);
    }

    @Test
    public void scheduler_paused_at_startup() throws InterruptedException {
        JobScheduler jobScheduler = createJobScheduler(true);
        CountJob job = new CountJob();
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        Thread.sleep(3000);
        assertTrue(job.getCount() == 0); // Job did not run
        // Resumes the execution and waits
        jobScheduler.resume();
        Thread.sleep(2000);
        // The job has run
        assertTrue(job.getCount() >= 2);
    }

    @Test
    public void scheduler_paused_at_startup_with_orchestration() throws InterruptedException {
        // Creates a job scheduler
        JobScheduler jobScheduler = createJobScheduler(true);
        // Job orchestration
        CountJob countJob = new CountJob();
        JobOrchestrator jobOrchestrator = new JobOrchestrator(
                jobScheduler,
                "Orchestrator",
                Collections.singletonList(
                        () -> Stream.of(
                                JobRegistration.of(countJob).withSchedule(Schedule.EVERY_SECOND)
                        )
                )
        );
        // Registers the orchestrator
        jobScheduler.schedule(jobOrchestrator, Schedule.EVERY_SECOND);
        // Waits some time...
        Thread.sleep(3000);
        // ... and the job should not have run
        assertEquals(countJob.getCount(), 0);
        // ... and the orchestrator must not have run
        Optional<JobStatus> orchestratorStatus = jobScheduler.getJobStatus(jobOrchestrator.getKey());
        assertTrue(
                orchestratorStatus.isPresent() &&
                        orchestratorStatus.get().getRunCount() == 0 &&
                        !orchestratorStatus.get().isRunning()
        );
    }

    @Test
    public void reschedule() throws InterruptedException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        // Initially every second
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        Thread.sleep(2500);
        int count = job.getCount();
        assertTrue(count == 3);
        // Then every minute
        jobScheduler.schedule(job, new Schedule(1, 1, TimeUnit.MINUTES));
        // Checks after three more seconds than the count has not moved
        Thread.sleep(3000);
        assertEquals(count, job.getCount());
    }

    @Test
    public void fire_immediately() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        // Fires far in the future
        jobScheduler.schedule(job, Schedule.everySeconds(60).after(60));
        assertEquals(0, job.getCount());
        // Fires immediately and waits for the result
        jobScheduler.fireImmediately(job.getKey()).orElseThrow(noFutureException).get(1, TimeUnit.SECONDS);
        assertEquals(1, job.getCount());
    }

    @Test
    public void fire_immediately_in_concurrency() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        LongCountJob job = new LongCountJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After 2 seconds, nothing has happened yet
        Thread.sleep(2000);
        assertEquals(0, job.getCount());
        // Checks its status
        Optional<JobStatus> jobStatus = jobScheduler.getJobStatus(job.getKey());
        assertTrue(jobStatus.isPresent() && jobStatus.get().isRunning());
        // Fires immediately and waits for the result
        Future<?> future = jobScheduler.fireImmediately(job.getKey()).orElse(null);
        assertNull("Job is not fired because already running", future);
        // The job is already running, count is still 0
        assertEquals(0, job.getCount());
        // Waits until completion
        Thread.sleep(2000);
        assertEquals(1, job.getCount());
    }

    @Test
    public void statuses() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();

        LongCountJob longCountJob = new LongCountJob();
        jobScheduler.schedule(longCountJob, Schedule.EVERY_SECOND);

        CountJob countJob = new CountJob();
        jobScheduler.schedule(countJob, Schedule.EVERY_SECOND.after(60));

        // After 2 seconds, the long job is already running, not the short one
        Thread.sleep(2000);
        Map<JobKey, JobStatus> statuses = jobScheduler.getJobStatuses().stream().collect(Collectors.toMap(
                JobStatus::getKey,
                status -> status
        ));

        JobStatus longStatus = statuses.get(longCountJob.getKey());
        assertTrue(longStatus.isRunning());

        JobStatus shortStatus = statuses.get(countJob.getKey());
        assertFalse(shortStatus.isRunning());
    }

    @Test
    public void removing_a_running_job() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After some seconds, the job keeps running
        Thread.sleep(2500);
        assertEquals(3, job.getCount());
        // Now, removes the job
        jobScheduler.unschedule(job.getKey());
        // Waits a bit, and checks the job has stopped running
        Thread.sleep(2500);
        assertEquals(3, job.getCount());
    }

    @Test
    public void removing_a_long_running_job() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        LongCountJob job = new LongCountJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // The job is now running
        Thread.sleep(1000);
        Optional<JobStatus> status = jobScheduler.getJobStatus(job.getKey());
        assertTrue(status.isPresent() && status.get().isRunning());
        // Now, removes the job
        jobScheduler.unschedule(job.getKey());
        // Waits a bit, and checks the job has stopped running
        Thread.sleep(1000);
        assertEquals(0, job.getCount());
    }

    @Test
    public void job_failures() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        ErrorJob job = new ErrorJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After some seconds, the job keeps running and has only failed
        Thread.sleep(2500);
        JobStatus status = jobScheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);
        assertEquals(3, status.getLastErrorCount());
        assertEquals("Failure", status.getLastError());
        // Now, fixes the job
        job.setFail(false);
        // Waits a bit, and checks the job is now OK
        Thread.sleep(2500);
        status = jobScheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);
        assertEquals(0, status.getLastErrorCount());
        assertNull(status.getLastError());
    }

    protected void test_with_pause(BiConsumer<JobScheduler, PauseableJob> pause, BiConsumer<JobScheduler, PauseableJob> resume) throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        PauseableJob job = new PauseableJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After some seconds, the job keeps running
        Thread.sleep(2500);
        assertEquals(3, job.getCount());
        // Pauses
        pause.accept(jobScheduler, job);
        // After some seconds, the job has not run
        Thread.sleep(2000);
        assertEquals(3, job.getCount());
        // Resumes the job
        resume.accept(jobScheduler, job);
        // After some seconds, the job has started again
        Thread.sleep(2000);
        assertEquals(5, job.getCount());
    }

    @Test
    public void job_pause() throws InterruptedException, ExecutionException, TimeoutException {
        test_with_pause(
                (jobScheduler, job) -> job.pause(),
                (jobScheduler, job) -> job.resume()
        );
    }

    @Test
    public void job_schedule_pause() throws InterruptedException, ExecutionException, TimeoutException {
        test_with_pause(
                (jobScheduler, job) -> jobScheduler.pause(job.getKey()),
                (jobScheduler, job) -> jobScheduler.resume(job.getKey())
        );
    }

    @Test
    public void scheduler_pause() throws InterruptedException, ExecutionException, TimeoutException {
        test_with_pause(
                (jobScheduler, job) -> jobScheduler.pause(),
                (jobScheduler, job) -> jobScheduler.resume()
        );
    }

    @Test
    public void stop() throws InterruptedException {
        JobScheduler jobScheduler = createJobScheduler();

        InterruptibleJob job = new InterruptibleJob();
        jobScheduler.schedule(job, Schedule.EVERY_MINUTE);

        // Waits for the start
        Thread.sleep(1500);

        // Checks it's running
        JobStatus jobStatus = jobScheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(jobStatus);
        assertTrue("Job is running", jobStatus.isRunning());

        // Stops the job
        System.out.println("Stopping the job.");
        assertTrue("Job has been stopped", jobScheduler.stop(job.getKey()));

        // Stopping the job is done asynchronously
        Thread.sleep(100);
        System.out.println("Checking job is stopped.");

        // Checks it has actually been stopped
        jobStatus = jobScheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull("Job is still scheduled", jobStatus);
        assertFalse("Job is actually stopped", jobStatus.isRunning());
    }

    @Test
    public void keys() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();

        LongCountJob longCountJob = new LongCountJob();
        CountJob countJob = new CountJob();

        jobScheduler.schedule(longCountJob, Schedule.EVERY_SECOND);
        jobScheduler.schedule(countJob, Schedule.EVERY_SECOND);
        OtherTypeJob otherTypeJob = new OtherTypeJob();

        jobScheduler.schedule(otherTypeJob, Schedule.EVERY_SECOND);

        assertEquals(
                ImmutableSet.of(longCountJob.getKey(), countJob.getKey(), otherTypeJob.getKey()),
                jobScheduler.getAllJobKeys()
        );

        assertEquals(
                ImmutableSet.of(longCountJob.getKey(), countJob.getKey()),
                jobScheduler.getJobKeysOfCategory(Fixtures.TEST_CATEGORY)
        );

        assertEquals(
                ImmutableSet.of(otherTypeJob.getKey()),
                jobScheduler.getJobKeysOfCategory(Fixtures.TEST_OTHER_CATEGORY)
        );
    }

    @Test(expected = JobNotScheduledException.class)
    public void pause_for_not_schedule_job() {
        JobScheduler jobScheduler = createJobScheduler();
        jobScheduler.pause(JobCategory.of("test").getType("test").getKey("x"));
    }

    @Test(expected = JobNotScheduledException.class)
    public void resume_for_not_schedule_job() {
        JobScheduler jobScheduler = createJobScheduler();
        jobScheduler.resume(JobCategory.of("test").getType("test").getKey("x"));
    }

    @Test(expected = JobNotScheduledException.class)
    public void fire_immediately_for_not_schedule_job() {
        JobScheduler jobScheduler = createJobScheduler();
        jobScheduler.fireImmediately(JobCategory.of("test").getType("test").getKey("x"));
    }

    @Test
    public void job_status_for_not_schedule_job() {
        JobScheduler jobScheduler = createJobScheduler();
        assertFalse(jobScheduler.getJobStatus(JobCategory.of("test").getType("test").getKey("x")).isPresent());
    }

    @Test
    public void invalid_job() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        ValidJob job = new ValidJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After some seconds, the job keeps running
        Thread.sleep(2500);
        assertEquals(3, job.getCount());
        // Invalidates the job
        job.invalidate();
        // The status indicates the job is no longer valid, but is still there
        JobStatus status = jobScheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);
        assertFalse(status.isValid());
        assertNull(status.getNextRunDate());
        // After some seconds, the job has not run
        Thread.sleep(1000);
        assertEquals(3, job.getCount());
        // ... and it's gone
        assertFalse(jobScheduler.getJobStatus(job.getKey()).isPresent());
    }

    @Test
    public void paused_job_can_be_fired() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        // Initially every second
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        Thread.sleep(2500);
        // After a few seconds, the count has moved
        assertEquals(3, job.getCount());
        // Pauses the job now
        jobScheduler.pause(job.getKey());
        // Not running
        Thread.sleep(2500);
        assertEquals(3, job.getCount());
        // Forcing the run
        jobScheduler.fireImmediately(job.getKey()).orElseThrow(noFutureException).get(1, TimeUnit.SECONDS);
        System.out.println("*****");
        assertEquals(4, job.getCount());
    }

    @Test
    public void not_scheduled_job_can_be_fired() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        // No schedule
        jobScheduler.schedule(job, Schedule.NONE);
        Thread.sleep(1500);
        // After a few seconds, the count has NOT moved
        assertEquals(0, job.getCount());
        // Forcing the run
        jobScheduler.fireImmediately(job.getKey()).orElseThrow(noFutureException).get(1, TimeUnit.SECONDS);
        assertEquals(1, job.getCount());
    }

    @Test
    public void not_scheduled_job_cannot_be_paused() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        CountJob job = new CountJob();
        // No schedule
        jobScheduler.schedule(job, Schedule.NONE);
        Thread.sleep(1500);
        // After a few seconds, the count has NOT moved
        assertEquals(0, job.getCount());
        // Pausing the job
        jobScheduler.pause(job.getKey());
        // Not paused
        JobStatus status = jobScheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);
        assertFalse(status.isPaused());
    }

    @Test
    public void disabled_job_cannot_be_fired() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        PauseableJob job = new PauseableJob();
        job.pause();
        // Initially every second
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        Thread.sleep(2500);
        // After a few seconds, the count has NOT moved
        assertEquals(0, job.getCount());
        // Forcing the run
        Optional<Future<?>> future = jobScheduler.fireImmediately(job.getKey());
        assertFalse("Job not fired", future.isPresent());
        // ... to not avail
        assertEquals(0, job.getCount());
    }

    @Test
    public void invalid_job_cannot_be_fired() throws InterruptedException, ExecutionException, TimeoutException {
        JobScheduler jobScheduler = createJobScheduler();
        ValidJob job = new ValidJob();
        job.invalidate();
        // Schedules, but not now
        jobScheduler.schedule(job, Schedule.EVERY_MINUTE.after(1));
        // Forcing the run
        Thread.sleep(2500);
        Optional<Future<?>> future = jobScheduler.fireImmediately(job.getKey());
        assertFalse("Job not fired", future.isPresent());
        // ... to not avail
        assertEquals(0, job.getCount());
        // ... and it's now gone
        assertFalse(jobScheduler.getJobStatus(job.getKey()).isPresent());
    }

}
