package net.nemerosa.ontrack.job;

import java.util.Collection;

public interface JobProvider {

    String getType();

    Collection<JobDefinition> getJobs();

}
