package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.NOPJobDecorator;
import net.nemerosa.ontrack.job.NOPJobListener;
import net.nemerosa.ontrack.job.Schedule;
import org.junit.Test;

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

        // Gets the

    }

}
