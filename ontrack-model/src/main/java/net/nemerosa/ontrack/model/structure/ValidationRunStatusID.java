package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.*;

@Data
public class ValidationRunStatusID {

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

}
