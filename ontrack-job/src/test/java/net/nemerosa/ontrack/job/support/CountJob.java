package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;
import net.nemerosa.ontrack.job.JobRunProgress;

public class CountJob implements Job {

    private int count = 0;

    @Override
    public JobKey getKey() {
        return new JobKey("test", "count");
    }

    public int getCount() {
        return count;
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            count++;
            listener.progress(JobRunProgress.message("Count = %s", count));
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
