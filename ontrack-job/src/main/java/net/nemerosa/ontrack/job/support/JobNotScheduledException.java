package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.job.JobKey;

public class JobNotScheduledException extends BaseException {
    public JobNotScheduledException(JobKey key) {
        super("Job with key %s is not scheduled.", key);
    }
}
