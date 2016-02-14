package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.job.JobKey;

public class JobSchedulerException extends BaseException {
    public JobSchedulerException(Exception ex, JobKey key) {
        super(ex, "Cannot schedule job: %s", key);
    }
}
