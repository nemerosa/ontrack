package net.nemerosa.ontrack.model.structure;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ValidationRunStatusService {

    Collection<ValidationRunStatusID> getValidationRunStatusList();

    ValidationRunStatusID getValidationRunStatus(String id);

    List<ValidationRunStatusID> getNextValidationRunStatusList(String id);

    default List<ValidationRunStatusID> getValidationRunStatusRoots() {
        return getValidationRunStatusList().stream()
                .filter(ValidationRunStatusID::isRoot)
                .collect(Collectors.toList());
    }

    void checkTransition(ValidationRunStatusID from, ValidationRunStatusID to);
}
