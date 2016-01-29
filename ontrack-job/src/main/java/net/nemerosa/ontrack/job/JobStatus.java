package net.nemerosa.ontrack.job;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class JobStatus {

    private final long id;
    private final JobKey key;
    private final Schedule schedule;
    private final String description;
    private final boolean running;
    private final boolean valid;
    private final boolean paused;
    private final boolean disabled;
    private final Map<String, ?> parameters;
    private final JobRunProgress progress;
    private final long runCount;
    private final LocalDateTime lastRunDate;
    private final long lastRunDurationMs;
    private final LocalDateTime nextRunDate;
    private final long lastErrorCount;
    private final String lastError;

}
