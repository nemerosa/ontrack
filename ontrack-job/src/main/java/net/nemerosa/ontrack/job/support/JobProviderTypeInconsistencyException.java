package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.job.JobCategory;
import net.nemerosa.ontrack.job.JobDefinitionProvider;
import net.nemerosa.ontrack.job.JobKey;

public class JobProviderTypeInconsistencyException extends BaseException {
    public JobProviderTypeInconsistencyException(Class<? extends JobDefinitionProvider> providerClass, JobCategory category, JobKey key) {
        super(
                "Job provider {} provides a job with key {} but only {} categories are accepted for this provider.",
                providerClass.getName(),
                key,
                category
        );
    }
}
