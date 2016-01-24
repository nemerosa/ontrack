package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;

public class ErrorJob implements Job {

    private boolean fail = true;

    @Override
    public JobKey getKey() {
        return new JobKey("test", "error");
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    @Override
    public Runnable getTask() {
        return () -> {
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
