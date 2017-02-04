package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.test.TestUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Test
    public void scatteringInSameType() {
        // Scheduler
        DefaultJobScheduler scheduler = new DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                new SynchronousScheduledExecutorService(),
                NOPJobListener.INSTANCE,
                false,
                true,
                1.0
        );
        // Creates a list of jobs with a weak key
        List<TestJob> jobs = TestUtils.range(1, 100).stream()
                .map(i -> TestJob.of(String.format("%d", i)))
                .collect(Collectors.toList());
        // Orchestration of all those jobs every 6 hours
        Collection<JobOrchestratorSupplier> jobOrchestratorSupplier = Collections.singletonList(
                () -> jobs.stream().map(j -> JobRegistration.of(j).everyMinutes(6 * 60))
        );
        // Orchestrator
        JobOrchestrator orchestrator = new JobOrchestrator(
                scheduler,
                "Orchestrator",
                jobOrchestratorSupplier
        );
        // Scheduling the orchestrator (manual mode)
        scheduler.schedule(orchestrator, Schedule.NONE);
        // Launching the orchestrator (manually)
        orchestrator.orchestrate(JobRunListener.out());
        // Getting the actual schedules of the jobs
        List<Schedule> actualSchedules = jobs.stream()
                .map(job -> scheduler.getJobStatus(job.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(JobStatus::getActualSchedule)
                .collect(Collectors.toList());
        List<Long> initialPeriods = actualSchedules.stream()
                .map(Schedule::getInitialPeriod)
                .collect(Collectors.toList());
        initialPeriods.forEach(l -> System.out.format("--> %d%n", l));
        // Checks that all jobs have been scheduled
        assertEquals("All jobs have been scheduled", jobs.size(), initialPeriods.size());
        // Checks that all schedules more or less different
        DescriptiveStatistics stats = new DescriptiveStatistics();
        initialPeriods.forEach(stats::addValue);
        // Gets the std deviation
        double standardDeviation = stats.getStandardDeviation();
        double max = stats.getMax();
        // Gets this in minutes (this was returned in ms)
        double stdDevMinutes = TimeUnit.MINUTES.convert((long) standardDeviation, TimeUnit.MILLISECONDS);
        double maxMinutes = TimeUnit.MINUTES.convert((long) max, TimeUnit.MILLISECONDS);
        // It must be >> 0
        assertTrue("Std deviation must be >> 0", stdDevMinutes > 60.0);
        System.out.println("Max = " + maxMinutes);
        assertTrue("Max is <= period", maxMinutes <= 6 * 60.0);
    }

}
