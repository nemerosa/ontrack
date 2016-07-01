package net.nemerosa.ontrack.job;

import lombok.Data;
import lombok.experimental.Wither;

/**
 * Association of a job and a schedule for a registration at startup.
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
