package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

@Deprecated
public class ValidJob implements Job {

    private int count = 0;
    private boolean valid = true;

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

    @Override
    public String getDescription() {
        return "Valid";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
