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
    private final String name;
    private final boolean root;
    private final boolean passed;
    private final Collection<String> followingStatuses;

    public ValidationRunStatusID addDependencies(String... followingStatuses) {
        List<String> dependencies = new ArrayList<>(this.followingStatuses);
        dependencies.addAll(Arrays.asList(followingStatuses));
        return new ValidationRunStatusID(id, name, root, passed, Collections.unmodifiableList(dependencies));
    }

    public static ValidationRunStatusID of(String id, String name, boolean root, boolean passed) {
        return new ValidationRunStatusID(id, name, root, passed, Collections.emptyList());
    }

}
