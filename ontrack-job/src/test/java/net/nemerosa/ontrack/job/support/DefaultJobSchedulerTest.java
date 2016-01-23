package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.NOPJobDecorator;
import net.nemerosa.ontrack.job.Schedule;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

}
