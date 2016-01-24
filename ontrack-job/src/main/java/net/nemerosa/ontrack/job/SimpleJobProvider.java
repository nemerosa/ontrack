package net.nemerosa.ontrack.job;

import java.util.Collection;
import java.util.LinkedList;

public class SimpleJobProvider implements JobProvider {

    private final String type;
    private final Collection<JobDefinition> jobs = new LinkedList<>();

    public SimpleJobProvider(String type) {
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

    public SimpleJobProvider withJobs(Schedule schedule, Job... jobs) {
        for (Job job : jobs) {
            this.jobs.add(new JobDefinition(job, schedule));
        }
        return this;
    }
}
