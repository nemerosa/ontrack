package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;

public class PauseableJob implements Job {

    private int count = 0;
    private boolean paused = false;

    @Override
    public JobKey getKey() {
        return new JobKey("test", "pauseable");
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
