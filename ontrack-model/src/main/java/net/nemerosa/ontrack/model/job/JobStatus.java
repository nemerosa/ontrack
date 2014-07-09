package net.nemerosa.ontrack.model.job;

import lombok.Data;
import net.nemerosa.ontrack.model.support.ApplicationInfo;

@Data
public class JobStatus {

    private final JobDescriptor descriptor;
    private final boolean running;
    private final ApplicationInfo info;

}
