package net.nemerosa.ontrack.job;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobStatus {

    private final JobKey key;
    private final Schedule schedule;
    private final String description;
    private final boolean running;
    private final JobRunProgress progress;
    private final long runCount;
    private final LocalDateTime lastRunDate;
    private final long lastRunDurationMs;
    private final LocalDateTime nextRunDate;
    private final long lastErrorCount;
    private final String lastError;

}
