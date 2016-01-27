package net.nemerosa.ontrack.job.builder;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;
import net.nemerosa.ontrack.job.JobType;

public class JobBuilder {

    private final JobType type;
    private final String id;
    private String description = "";
    private Runnable task = () -> {
    };
    private boolean disabled = false;

    public JobBuilder(JobType type, String id) {
        this.type = type;
        this.id = id;
    }

    public JobBuilder withDescription(String value) {
        this.description = value;
        return this;
    }

    public JobBuilder withTask(Runnable value) {
        this.task = value;
        return this;
    }

    public JobBuilder withDisabled(boolean value) {
        this.disabled = value;
        return this;
    }

    public Job build() {
        return new Job() {
            @Override
            public JobKey getKey() {
                return new JobKey(type, id);
            }

            @Override
            public JobRun getTask() {
                return (listener) -> task.run();
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public boolean isDisabled() {
                return disabled;
            }
        };
    }

    public static JobBuilder create(JobType type, String id) {
        return new JobBuilder(type, id);
    }

}
