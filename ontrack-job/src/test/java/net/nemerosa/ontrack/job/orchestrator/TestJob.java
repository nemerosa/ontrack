package net.nemerosa.ontrack.job.orchestrator;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobCategory;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

/**
 * @deprecated Use {@link net.nemerosa.ontrack.job.support.TestJob} instead.
 */
@Deprecated
public class TestJob implements Job {

    private final String name;

    public TestJob(String name) {
        this.name = name;
    }

    public static JobKey getKey(String name) {
        return JobCategory.of("test").getType("orchestrator").getKey(name);
    }

    @Override
    public JobKey getKey() {
        return getKey(name);
    }

    @Override
    public JobRun getTask() {
        return runListener -> runListener.message(name);
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
