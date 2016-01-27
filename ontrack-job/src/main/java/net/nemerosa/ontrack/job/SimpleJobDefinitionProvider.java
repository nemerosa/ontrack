package net.nemerosa.ontrack.job;

import java.util.Collection;
import java.util.LinkedList;

public class SimpleJobDefinitionProvider implements JobDefinitionProvider {

    private final String type;
    private Collection<JobDefinition> jobs = new LinkedList<>();

    public SimpleJobDefinitionProvider(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Collection<JobDefinition> getJobs() {
        return jobs;
    }

    public void setJobs(Schedule schedule, Job... jobs) {
        this.jobs.clear();
        for (Job job : jobs) {
            this.jobs.add(new JobDefinition(job, schedule));
        }
    }
}
