package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

public class LongCountJob implements Job {

    private int count = 0;

    @Override
    public JobKey getKey() {
        return new JobKey("test", "long-count");
    }

    public int getCount() {
        return count;
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count++;
            System.out.println("Count = " + count);
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
