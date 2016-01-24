package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;

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
    public Runnable getTask() {
        return () -> {
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
