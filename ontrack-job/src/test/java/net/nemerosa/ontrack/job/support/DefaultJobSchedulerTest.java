package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.NOPJobDecorator;
import net.nemerosa.ontrack.job.Schedule;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(0, job.getCount());
        // Fires immediately and waits for the result
        Future<?> future = jobScheduler.fireImmediately(job.getKey());
        // The job is already running, count is still 0
        assertEquals(0, job.getCount());
        // Waits until completion
        future.get(1, TimeUnit.MINUTES);
        assertEquals(1, job.getCount());
    }

}
