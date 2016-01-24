package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.builder.JobBuilder;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

public class DefaultJobPortalTest {

    @Test
    public void portal() throws InterruptedException {
        JobScheduler jobScheduler = new DefaultJobScheduler(NOPJobDecorator.INSTANCE, Executors.newSingleThreadScheduledExecutor(), NOPJobListener.INSTANCE);
        JobPortal jobPortal = new DefaultJobPortal(jobScheduler, Schedule.EVERY_MINUTE);

        // Counts
        AtomicLong count1 = new AtomicLong();
        AtomicLong count2 = new AtomicLong();
        AtomicLong count3 = new AtomicLong();

        // Job creation
        BiFunction<Integer, AtomicLong, Job> jobCreation = (no, atomicLong) ->
                JobBuilder.create("test", "job-" + no)
                        .withTask(atomicLong::incrementAndGet)
                        .build();

        // Jobs to register
        Job job1 = jobCreation.apply(1, count1);
        Job job2 = jobCreation.apply(2, count2);
        Job job3 = jobCreation.apply(3, count3);

        // Job provider
        JobProvider jobProvider = new SimpleJobProvider("test")
                .withJobs(Schedule.EVERY_SECOND, job1, job2, job3);

        // Registration in the portal
        jobPortal.registerJobProvider(jobProvider);

        // Waits some time
        Thread.sleep(2500);

        // Counts must have been increased
        assertEquals(3, count1.get());
        assertEquals(3, count2.get());
        assertEquals(3, count3.get());
    }

}
