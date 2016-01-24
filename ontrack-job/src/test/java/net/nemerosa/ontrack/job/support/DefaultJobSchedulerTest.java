package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DefaultJobSchedulerTest {

    @Test
    public void schedule() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);
        CountJob job = new CountJob();
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        Thread.sleep(3000);
        assertTrue(job.getCount() >= 2);
    }

    @Test
    public void reschedule() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);
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
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);
        CountJob job = new CountJob();
        // Fires far in the future
        jobScheduler.schedule(job, Schedule.everySeconds(60).after(60));
        assertEquals(0, job.getCount());
        // Fires immediately and waits for the result
        jobScheduler.fireImmediately(job.getKey()).get(1, TimeUnit.SECONDS);
        assertEquals(1, job.getCount());
    }

    @Test
    public void fire_immediately_in_concurrency() throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);
        LongCountJob job = new LongCountJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After 2 seconds, nothing has happened yet
        Thread.sleep(2000);
        assertEquals(0, job.getCount());
        // Checks its status
        assertTrue(jobScheduler.getJobStatus(job.getKey()).isRunning());
        // Fires immediately and waits for the result
        Future<?> future = jobScheduler.fireImmediately(job.getKey());
        // The job is already running, count is still 0
        assertEquals(0, job.getCount());
        // Waits until completion
        future.get(1, TimeUnit.MINUTES);
        assertEquals(1, job.getCount());
    }

    @Test
    public void statuses() throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);

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
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);
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
    public void job_failures() throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService);
        ErrorJob job = new ErrorJob();
        // Fires now
        jobScheduler.schedule(job, Schedule.EVERY_SECOND);
        // After some seconds, the job keeps running and has only failed
        Thread.sleep(2500);
        JobStatus status = jobScheduler.getJobStatus(job.getKey());
        assertEquals(3, status.getLastErrorCount());
        assertEquals("Failure", status.getLastError());
        // Now, fixes the job
        job.setFail(false);
        // Waits a bit, and checks the job is now OK
        Thread.sleep(2500);
        status = jobScheduler.getJobStatus(job.getKey());
        assertEquals(0, status.getLastErrorCount());
        assertNull(status.getLastError());
    }

}
