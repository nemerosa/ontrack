package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.Collection;

@Data
public class ValidationRunStatusID {

    private final String id;
    private final Collection<ValidationRunStatusID> followingStatuses;

    public boolean isRoot() {
        return followingStatuses.isEmpty();
    }

}
