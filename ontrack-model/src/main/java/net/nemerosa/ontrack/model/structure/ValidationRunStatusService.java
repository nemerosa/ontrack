package net.nemerosa.ontrack.model.structure;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ValidationRunStatusService {

    Collection<ValidationRunStatusID> getValidationRunStatusList();

    Optional<ValidationRunStatusID> getValidationRunStatus(String id);

    default Collection<ValidationRunStatusID> getValidationRunStatusRoots() {
        return getValidationRunStatusList().stream()
                .filter(ValidationRunStatusID::isRoot)
                .collect(Collectors.toList());
    }

}
