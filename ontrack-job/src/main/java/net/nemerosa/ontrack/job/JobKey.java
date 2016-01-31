package net.nemerosa.ontrack.job;

import lombok.Value;

import java.util.Objects;

@Value
public class JobKey {

    private final JobType type;
    private final String id;

    public static JobKey of(JobType type, String id) {
        return new JobKey(type, id);
    }

    public boolean sameType(JobType type) {
        return Objects.equals(this.type, type);
    }

    public boolean sameCategory(JobCategory category) {
        return Objects.equals(this.type.getCategory(), category);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[%s]",
                type,
                id
        );
    }
}
