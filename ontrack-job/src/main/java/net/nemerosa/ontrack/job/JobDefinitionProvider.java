package net.nemerosa.ontrack.job;

import java.util.Collection;

public interface JobDefinitionProvider {

    String getType();

    Collection<JobDefinition> getJobs();

}
