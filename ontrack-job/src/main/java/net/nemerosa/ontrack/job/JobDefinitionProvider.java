package net.nemerosa.ontrack.job;

import java.util.Collection;

@Deprecated
public interface JobDefinitionProvider {

    JobCategory getJobCategory();

    Collection<JobDefinition> getJobs();

}
