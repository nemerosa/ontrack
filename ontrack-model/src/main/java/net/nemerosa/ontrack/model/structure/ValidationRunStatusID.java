package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.*;

@Data
public class ValidationRunStatusID {

    public static final String DEFECTIVE = "DEFECTIVE";
    public static final String EXPLAINED = "EXPLAINED";
    public static final String FAILED = "FAILED";
    public static final String FIXED = "FIXED";
    public static final String INTERRUPTED = "INTERRUPTED";
    public static final String INVESTIGATING = "INVESTIGATING";
    public static final String PASSED = "PASSED";
    public static final String WARNING = "WARNING";

    private final String id;
    // TODO Status name
    private final Collection<String> followingStatuses;

    public boolean isRoot() {
        return followingStatuses.isEmpty();
    }

    public ValidationRunStatusID addDependencies(String... followingStatuses) {
        List<String> dependencies = new ArrayList<>(this.followingStatuses);
        dependencies.addAll(Arrays.asList(followingStatuses));
        return new ValidationRunStatusID(id, Collections.unmodifiableList(dependencies));
    }

    public static ValidationRunStatusID of(String id) {
        return new ValidationRunStatusID(id, Collections.emptyList());
    }

    public boolean isPassed() {
        return PASSED.equals(id) || FIXED.equals(id);
    }
}
