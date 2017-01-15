package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

@Deprecated
public class PauseableJob implements Job {

    private int count = 0;
    private boolean paused = false;

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_CATEGORY.getType("pauseable").getKey("pauseable");
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

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    @Override
    public String getDescription() {
        return "Pauseable";
    }

    @Override
    public boolean isDisabled() {
        return paused;
    }
}
