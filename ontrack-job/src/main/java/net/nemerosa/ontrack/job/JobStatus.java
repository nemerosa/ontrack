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

    public JobState getState() {
        if (running) {
            return JobState.RUNNING;
        } else if (!valid) {
            return JobState.INVALID;
        } else if (disabled) {
            return JobState.DISABLED;
        } else if (paused) {
            return JobState.PAUSED;
        } else {
            return JobState.IDLE;
        }
    }

    public boolean canRun() {
        return !running
                && !disabled
                && valid;
    }

    public boolean canPause() {
        return schedule.getPeriod() > 0
                && !paused
                && !disabled
                && valid;
    }

    public boolean canResume() {
        return paused
                && !disabled
                && valid;
    }

    public boolean isError() {
        return lastErrorCount > 0;
    }

    public boolean canBeDeleted() {
        return !valid;
    }

    public String getProgressText() {
        return progress != null ? progress.getText() : "";
    }

    public boolean canBeStopped() {
        return running;
    }
}
