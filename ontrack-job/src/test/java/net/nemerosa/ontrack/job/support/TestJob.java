package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

public class TestJob implements Job {

    public static TestJob of() {
        return new TestJob();
    }

    private int count = 0;
    private boolean valid = true;
    private boolean disabled = false;

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_CATEGORY.getType("valid").getKey("valid");
    }

    public int getCount() {
        return count;
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            count++;
            System.out.println("Count = " + count);
        };
    }

    public void invalidate() {
        this.valid = false;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String getDescription() {
        return "Test job";
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
