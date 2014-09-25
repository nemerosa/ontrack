package net.nemerosa.ontrack.model.job;

import lombok.Data;
import net.nemerosa.ontrack.model.support.ApplicationInfo;

import java.time.LocalDateTime;

@Data
public class JobStatus {

    private final long id;
    private final JobDescriptor descriptor;
    private final boolean running;
    private final ApplicationInfo info;
    private final long runCount;
    private final LocalDateTime lastRunDate;
    private final long lastRunDurationMs;
    private final LocalDateTime nextRunDate;

}
