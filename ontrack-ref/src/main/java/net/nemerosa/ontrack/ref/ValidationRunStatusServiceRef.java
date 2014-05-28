package net.nemerosa.ontrack.ref;

import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusUnknownDependencyException;
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID;
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService;
import net.nemerosa.ontrack.model.support.StartupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.structure.ValidationRunStatusID.*;

@Service
public class ValidationRunStatusServiceRef implements ValidationRunStatusService, StartupService {

    private final Logger logger = LoggerFactory.getLogger(ValidationRunStatusService.class);

    private final Map<String, ValidationRunStatusID> statuses = new LinkedHashMap<>();

    @Override
    public Collection<ValidationRunStatusID> getValidationRunStatusList() {
        return statuses.values();
    }

    @Override
    public ValidationRunStatusID getValidationRunStatus(String id) {
        return Optional.ofNullable(statuses.get(id)).orElseThrow(() -> new ValidationRunStatusNotFoundException(id));
    }

    @Override
    public Collection<ValidationRunStatusID> getNextValidationRunStatusList(String id) {
        return getValidationRunStatus(id).getFollowingStatuses().stream()
                .map(this::getValidationRunStatus)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "Loading of validation run statuses";
    }

    @Override
    public int startupOrder() {
        return 10;
    }

    /**
     * Registers the tree of validation run status ids.
     */
    @Override
    public void start() {
        register(ValidationRunStatusID.of(PASSED, "Passed", true, true));
        register(ValidationRunStatusID.of(WARNING, "Warning", true, true));
        register(ValidationRunStatusID.of(FIXED, "Fixed", false, true));
        register(ValidationRunStatusID.of(DEFECTIVE, "Defective", false, false));
        register(ValidationRunStatusID.of(EXPLAINED, "Explained", false, false), FIXED);
        register(ValidationRunStatusID.of(INVESTIGATING, "Investigating", true, false), DEFECTIVE, EXPLAINED, FIXED);
        register(ValidationRunStatusID.of(INTERRUPTED, "Interrupted", true, false), INVESTIGATING, FIXED);
        register(ValidationRunStatusID.of(FAILED, "Failed", true, false), INTERRUPTED, INVESTIGATING, EXPLAINED, DEFECTIVE);
        // TODO Participation from extensions
        // Checks the tree
        for (ValidationRunStatusID statusID : statuses.values()) {
            for (String nextStatus : statusID.getFollowingStatuses()) {
                if (!statuses.containsKey(nextStatus)) {
                    throw new ValidationRunStatusUnknownDependencyException(statusID.getId(), nextStatus);
                }
            }
        }
        // Logging
        for (ValidationRunStatusID statusID : statuses.values()) {
            logger.info(
                    "[status] {} -> {}",
                    statusID.getId(),
                    StringUtils.join(statusID.getFollowingStatuses(), ",")
            );
        }
    }

    private void register(ValidationRunStatusID statusID, String... next) {
        statuses.put(statusID.getId(), statusID.addDependencies(next));
    }

}
