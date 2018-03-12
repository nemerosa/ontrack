package net.nemerosa.ontrack.job.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.job.*;

/**
 * @deprecated Use {@link ConfigurableJob} from Kotlin test classes
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Deprecated
public class TestJob implements Job {

    public static JobKey key(String name) {
        return Fixtures.TEST_CATEGORY.getType("test").getKey(name);
    }

    public static TestJob of() {
        return of("test");
    }

    public static TestJob of(String name) {
        return new TestJob(
                name,
                0,
                Fixtures.TEST_CATEGORY,
                "test",
                false,
                0L,
                true,
                false
        );
    }

    private final String name;
    private int count = 0;
    @Wither
    private JobCategory category;
    @Wither
    private String type;
    @Wither
    private boolean fail = false;
    @Wither
    private long wait = 0;
    private boolean valid = true;
    private boolean disabled = false;

    @Override
    public JobKey getKey() {
        return category.getType(type).getKey(name);
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
            if (fail) {
                throw new RuntimeException("Task failure");
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
