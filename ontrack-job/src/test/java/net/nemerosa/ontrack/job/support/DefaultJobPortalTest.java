package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.builder.JobBuilder;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

public class DefaultJobPortalTest {

    private final BiFunction<Integer, AtomicLong, Job> jobCreation = (no, atomicLong) ->
            JobBuilder.create("test", "job-" + no)
                    .withTask(atomicLong::incrementAndGet)
                    .build();

    protected JobPortal createJobPortal() {
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, Executors.newSingleThreadScheduledExecutor(), NOPJobListener.INSTANCE);
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
        SimpleJobProvider jobProvider = new SimpleJobProvider("test");
        jobProvider.setJobs(Schedule.EVERY_SECOND, job1, job2, job3);

        // Registration in the portal
        jobPortal.registerJobProvider(jobProvider);

        // Waits some time
        Thread.sleep(2500);

        // Counts must have been increased
        assertEquals(3, count1.get());
        assertEquals(3, count2.get());
        assertEquals(3, count3.get());
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
        SimpleJobProvider jobProvider = new SimpleJobProvider("test");
        jobProvider.setJobs(Schedule.EVERY_SECOND, job1, job2, job3);

        // Registration in the portal
        jobPortal.registerJobProvider(jobProvider);

        // Waits some time
        Thread.sleep(2500);

        // Counts must have been increased
        assertEquals(3, count1.get());
        assertEquals(3, count2.get());
        assertEquals(3, count3.get());
        assertEquals(0, count4.get());

        // Removes one job, adds another
        jobProvider.setJobs(Schedule.EVERY_SECOND, job1, job2, job4);

        // Waits some time
        Thread.sleep(3000);

        // Counts must have been increased, and some must have stopped
        assertEquals(6, count1.get());
        assertEquals(6, count2.get());
        assertEquals(4, count3.get());
        assertEquals(3, count4.get());

    }

}
