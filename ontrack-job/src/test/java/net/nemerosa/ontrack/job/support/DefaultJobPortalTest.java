package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.builder.JobBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultJobPortalTest {

    private final BiFunction<Integer, AtomicLong, Job> jobCreation = (no, atomicLong) ->
            JobBuilder.create("test", "job-" + no)
                    .withTask(atomicLong::incrementAndGet)
                    .build();

    private ScheduledExecutorService scheduledExecutorService;

    @Before
    public void before() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @After
    public void after() {
        scheduledExecutorService.shutdownNow();
    }

    protected JobPortal createJobPortal() {
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, scheduledExecutorService, NOPJobListener.INSTANCE);
        return new DefaultJobPortal(jobScheduler, Schedule.EVERY_SECOND);
    }

    @Test
    public void portal() throws InterruptedException {
        JobPortal jobPortal = createJobPortal();

        // Counts
        AtomicLong count1 = new AtomicLong();
        AtomicLong count2 = new AtomicLong();
        AtomicLong count3 = new AtomicLong();

        // Jobs to register
        Job job1 = jobCreation.apply(1, count1);
        Job job2 = jobCreation.apply(2, count2);
        Job job3 = jobCreation.apply(3, count3);

        // Job provider
        SimpleJobDefinitionProvider jobProvider = new SimpleJobDefinitionProvider("test");
        jobProvider.setJobs(Schedule.EVERY_SECOND, job1, job2, job3);

        // Registration in the portal
        jobPortal.registerJobProvider(jobProvider);

        // Waits some time
        Thread.sleep(2500);

        // Counts must have been increased
        assertTrue(count1.get() >= 2);
        assertTrue(count2.get() >= 2);
        assertTrue(count3.get() >= 2);
    }

    @Test
    public void reschedule() throws InterruptedException {
        JobPortal jobPortal = createJobPortal();

        // Counts
        AtomicLong count1 = new AtomicLong();
        AtomicLong count2 = new AtomicLong();
        AtomicLong count3 = new AtomicLong();
        AtomicLong count4 = new AtomicLong();

        // Job creation

        // Jobs to register
        Job job1 = jobCreation.apply(1, count1);
        Job job2 = jobCreation.apply(2, count2);
        Job job3 = jobCreation.apply(3, count3);
        Job job4 = jobCreation.apply(4, count4);

        // Job provider
        SimpleJobDefinitionProvider jobProvider = new SimpleJobDefinitionProvider("test");
        jobProvider.setJobs(Schedule.EVERY_SECOND, job1, job2, job3);

        // Registration in the portal
        jobPortal.registerJobProvider(jobProvider);

        // Waits some time
        Thread.sleep(2500);

        // Counts must have been increased
        assertTrue(count1.get() >= 2);
        assertTrue(count2.get() >= 2);
        assertTrue(count3.get() >= 2);
        assertEquals(0, count4.get());

        // Removes one job, adds another
        jobProvider.setJobs(Schedule.EVERY_SECOND, job1, job2, job4);

        // Waits some time
        Thread.sleep(4500);

        // Counts must have been increased, and some must have stopped
        assertTrue(count1.get() >= 6);
        assertTrue(count2.get() >= 6);
        assertTrue(count3.get() <= 4); // The job 3 might have been still running when it was unscheduled
        assertTrue(count4.get() >= 3);

    }

}
