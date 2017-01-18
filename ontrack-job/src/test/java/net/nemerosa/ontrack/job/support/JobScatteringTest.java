package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.job.NOPJobDecorator;
import net.nemerosa.ontrack.job.NOPJobListener;
import net.nemerosa.ontrack.job.Schedule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class JobScatteringTest {

    @Test
    public void scatteredScheduleDisabled() {
        DefaultJobScheduler scheduler = new DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                new SynchronousScheduledExecutorService(),
                NOPJobListener.INSTANCE,
                false,
                false,
                1.0
        );

        TestJob job = TestJob.of();
        scheduler.schedule(job, Schedule.everyMinutes(30));

        // Gets the schedule of the job
        JobStatus status = scheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);

        assertEquals(0L, status.getSchedule().getInitialPeriod());
        assertEquals(30L, status.getSchedule().getPeriod());
        assertEquals(TimeUnit.MINUTES, status.getSchedule().getUnit());

        assertEquals(0L, status.getActualSchedule().getInitialPeriod());
        assertEquals(30 * 60 * 1000L, status.getActualSchedule().getPeriod());
        assertEquals(TimeUnit.MILLISECONDS, status.getActualSchedule().getUnit());
    }

    @Test
    public void scatteredScheduleEnabled() {
        DefaultJobScheduler scheduler = new DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                new SynchronousScheduledExecutorService(),
                NOPJobListener.INSTANCE,
                false,
                true,
                0.5
        );

        TestJob job = TestJob.of();
        scheduler.schedule(job, Schedule.everyMinutes(30));

        // Gets the schedule of the job
        JobStatus status = scheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);

        assertEquals(0L, status.getSchedule().getInitialPeriod());
        assertEquals(30L, status.getSchedule().getPeriod());
        assertEquals(TimeUnit.MINUTES, status.getSchedule().getUnit());

        long actualInitialPeriod = status.getActualSchedule().getInitialPeriod();
        assertTrue(actualInitialPeriod >= 0L);
        assertTrue(actualInitialPeriod <= 15 * 60 * 1000L);
        assertEquals(30 * 60 * 1000L, status.getActualSchedule().getPeriod());
        assertEquals(TimeUnit.MILLISECONDS, status.getActualSchedule().getUnit());
    }

    @Test
    public void scatteredScheduleEnabledWithInitialDelay() {
        DefaultJobScheduler scheduler = new DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                new SynchronousScheduledExecutorService(),
                NOPJobListener.INSTANCE,
                false,
                true,
                0.5
        );

        TestJob job = TestJob.of();
        scheduler.schedule(job, Schedule.everyMinutes(30).after(10));

        // Gets the schedule of the job
        JobStatus status = scheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);

        assertEquals(10L, status.getSchedule().getInitialPeriod());
        assertEquals(30L, status.getSchedule().getPeriod());
        assertEquals(TimeUnit.MINUTES, status.getSchedule().getUnit());

        long actualInitialPeriod = status.getActualSchedule().getInitialPeriod();
        assertTrue(actualInitialPeriod >= 10 * 60 * 1000L);
        assertTrue(actualInitialPeriod <= 40 * 60 * 1000L);
        assertEquals(30 * 60 * 1000L, status.getActualSchedule().getPeriod());
        assertEquals(TimeUnit.MILLISECONDS, status.getActualSchedule().getUnit());
    }

    @Test
    public void scatteredScheduleEnabledWithNoSchedule() {
        DefaultJobScheduler scheduler = new DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                new SynchronousScheduledExecutorService(),
                NOPJobListener.INSTANCE,
                false,
                true,
                0.5
        );

        TestJob job = TestJob.of();
        scheduler.schedule(job, Schedule.NONE);

        // Gets the schedule of the job
        JobStatus status = scheduler.getJobStatus(job.getKey()).orElse(null);
        assertNotNull(status);

        assertEquals(0L, status.getSchedule().getInitialPeriod());
        assertEquals(0L, status.getSchedule().getPeriod());
        assertEquals(TimeUnit.SECONDS, status.getSchedule().getUnit());

        assertEquals(0L, status.getActualSchedule().getInitialPeriod());
        assertEquals(0L, status.getActualSchedule().getPeriod());
        assertEquals(TimeUnit.MILLISECONDS, status.getActualSchedule().getUnit());
    }

}
