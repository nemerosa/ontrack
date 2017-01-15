package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

@Deprecated
public class OtherTypeJob implements Job {

    private int count = 0;

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_OTHER_CATEGORY.getType("count").getKey("count");
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

    @Override
    public String getDescription() {
        return "Count";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
