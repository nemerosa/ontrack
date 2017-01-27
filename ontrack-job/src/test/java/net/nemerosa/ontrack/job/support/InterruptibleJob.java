package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

public class InterruptibleJob implements Job {

    private int count = 0;

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_CATEGORY.getType("interruptible-count").getKey("interruptible-count");
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            while (count < 50) {
                try {
                    listener.message("IN JOB - running for count %d", count);
                    Thread.sleep(100);
                    count++;
                } catch (InterruptedException e) {
                    listener.message("IN JOB - interrupted");
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public String getDescription() {
        return "Long count";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
