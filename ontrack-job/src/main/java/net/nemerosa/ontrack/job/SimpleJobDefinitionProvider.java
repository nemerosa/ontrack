package net.nemerosa.ontrack.job;

import java.util.Collection;
import java.util.LinkedList;

public class SimpleJobDefinitionProvider implements JobDefinitionProvider {

    private final JobType type;
    private Collection<JobDefinition> jobs = new LinkedList<>();

    public SimpleJobDefinitionProvider(JobType type) {
        this.type = type;
    }

    @Override
    public JobCategory getJobCategory() {
        return type.getCategory();
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
