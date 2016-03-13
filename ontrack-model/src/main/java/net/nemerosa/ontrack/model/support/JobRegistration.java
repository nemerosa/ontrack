package net.nemerosa.ontrack.model.support;

import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.Schedule;

/**
 * Association of a job and a schedule for a registration at startup.
 *
 * @see JobProvider
 */
@Data
public class JobRegistration {

    private final Job job;
    @Wither
    private final Schedule schedule;

    public static JobRegistration of(Job job) {
        return new JobRegistration(job, Schedule.NONE);
    }

    public JobRegistration everyMinutes(long minutes) {
        return withSchedule(Schedule.everyMinutes(minutes));
    }
}
