package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

@Deprecated
public class ErrorJob implements Job {

    private boolean fail = true;

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_CATEGORY.getType("error").getKey("error");
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            System.out.println("Failing = " + fail);
            if (fail) {
                throw new RuntimeException("Failure");
            }
        };
    }

    @Override
    public String getDescription() {
        return "Failure";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
