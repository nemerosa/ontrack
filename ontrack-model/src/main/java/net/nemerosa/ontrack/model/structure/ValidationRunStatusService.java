package net.nemerosa.ontrack.model.structure;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ValidationRunStatusService {

    String DEFECTIVE = "DEFECTIVE";
    String EXPLAINED = "EXPLAINED";
    String FAILED = "FAILED";
    String FIXED = "FIXED";
    String INTERRUPTED = "INTERRUPTED";
    String INVESTIGATING = "INVESTIGATING";
    String PASSED = "PASSED";
    String WARNING = "WARNING";

    Collection<ValidationRunStatusID> getValidationRunStatusList();

    ValidationRunStatusID getValidationRunStatus(String id);

    Collection<ValidationRunStatusID> getNextValidationRunStatusList(String id);

    default List<ValidationRunStatusID> getValidationRunStatusRoots() {
        return getValidationRunStatusList().stream()
                .filter(ValidationRunStatusID::isRoot)
                .collect(Collectors.toList());
    }

}
