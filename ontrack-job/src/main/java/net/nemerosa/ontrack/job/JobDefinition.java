package net.nemerosa.ontrack.job;

import lombok.Data;

@Data
@Deprecated
public class JobDefinition {

    private final Job job;
    private final Schedule schedule;

    public static JobDefinitionBuilder withJob(Job job) {
        return new JobDefinitionBuilder(job);
    }

    public static class JobDefinitionBuilder {

        private final Job job;

        private JobDefinitionBuilder(Job job) {
            this.job = job;
        }

        public JobDefinition withSchedule(Schedule schedule) {
            return new JobDefinition(job, schedule);
        }
    }

}
