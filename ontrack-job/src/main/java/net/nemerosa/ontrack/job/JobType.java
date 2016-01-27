package net.nemerosa.ontrack.job;

import lombok.Value;
import lombok.experimental.Wither;

@Value
public class JobType {

    private final JobCategory category;
    private final String key;
    @Wither
    private final String name;

    public static JobType of(JobCategory category, String key) {
        return new JobType(category, key, key);
    }

    public JobKey getKey(String id) {
        return JobKey.of(this, id);
    }

    @Override
    public String toString() {
        return String.format(
                "[%s][%s]",
                category,
                key
        );
    }

}
