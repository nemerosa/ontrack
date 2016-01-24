package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobProvider;

public class JobProviderTypeInconsistencyException extends BaseException {
    public JobProviderTypeInconsistencyException(Class<? extends JobProvider> providerClass, String type, JobKey key) {
        super(
                "Job provider {} provides a job with key {} but only {} types are accepted for this provider.",
                providerClass.getName(),
                key,
                type
        );
    }
}
