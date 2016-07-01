package net.nemerosa.ontrack.job;

import lombok.Value;
import lombok.experimental.Wither;

@Value
public class JobCategory {

    /**
     * Core category, used internally
     */
    public static final JobCategory CORE = JobCategory.of("core").withName("Core");

    private final String key;
    @Wither
    private final String name;

    public static JobCategory of(String key) {
        return new JobCategory(key, key);
    }

    public JobType getType(String key) {
        return JobType.of(this, key);
    }

    @Override
    public String toString() {
        return String.format(
                "[%s]",
                key
        );
    }

}
