package net.nemerosa.ontrack.job.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.job.Fixtures;
import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobRun;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TestJob implements Job {

    public static TestJob of() {
        return of("test");
    }

    public static TestJob of(String name) {
        return new TestJob(name, 0, 0L, true, false);
    }

    private final String name;
    private int count = 0;
    @Wither
    private long wait = 0;
    private boolean valid = true;
    private boolean disabled = false;

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_CATEGORY.getType(name).getKey(name);
    }

    public int getCount() {
        return count;
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Job was interrupted", e);
                }
            }
            count++;
            listener.message("TEST JOB %s Count = %d", name, count);
        };
    }

    public void invalidate() {
        this.valid = false;
    }

    public void pause() {
        setDisabled(true);
    }

    public void resume() {
        setDisabled(false);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String getDescription() {
        return "Test job";
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
