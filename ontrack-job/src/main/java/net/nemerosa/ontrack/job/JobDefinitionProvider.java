package net.nemerosa.ontrack.job;

import java.util.Collection;

public interface JobDefinitionProvider {

    JobCategory getJobCategory();

    Collection<JobDefinition> getJobs();

}
