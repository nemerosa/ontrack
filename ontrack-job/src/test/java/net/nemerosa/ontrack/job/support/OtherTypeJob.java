package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;

public class OtherTypeJob implements Job {

    private int count = 0;

    @Override
    public JobKey getKey() {
        return new JobKey("test-other", "count");
    }

    public int getCount() {
        return count;
    }

    @Override
    public Runnable getTask() {
        return () -> {
            count++;
            System.out.println("Count = " + count);
        };
    }

    @Override
    public String getDescription() {
        return "Count";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
