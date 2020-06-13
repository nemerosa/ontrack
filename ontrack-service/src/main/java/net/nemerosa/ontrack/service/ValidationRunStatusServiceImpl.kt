package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusChangeForbiddenException;
import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusUnknownDependencyException;
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID;
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService;
import net.nemerosa.ontrack.model.support.StartupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.structure.ValidationRunStatusID.*;

@Service
public class ValidationRunStatusServiceImpl implements ValidationRunStatusService, StartupService {

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
    public List<ValidationRunStatusID> getNextValidationRunStatusList(String id) {
        return getValidationRunStatus(id).getFollowingStatuses().stream()
                .map(this::getValidationRunStatus)
                .collect(Collectors.toList());
    }

    @Override
    public void checkTransition(ValidationRunStatusID from, ValidationRunStatusID to) {
        if (!from.getFollowingStatuses().contains(to.getId())) {
            throw new ValidationRunStatusChangeForbiddenException(from.getId(), to.getId());
        }
    }

    @Override
    public String getName() {
        return "Loading of validation run statuses";
    }

    @Override
    public int startupOrder() {
        return SYSTEM_REGISTRATION;
    }

    /**
     * Registers the tree of validation run status ids.
     */
    @Override
    public void start() {
        register(STATUS_PASSED);
        register(STATUS_FIXED);
        register(STATUS_DEFECTIVE);
        register(STATUS_EXPLAINED, FIXED);
        register(STATUS_INVESTIGATING, DEFECTIVE, EXPLAINED, FIXED);
        register(STATUS_INTERRUPTED, INVESTIGATING, FIXED);
        register(STATUS_FAILED, INTERRUPTED, INVESTIGATING, EXPLAINED, DEFECTIVE);
        register(STATUS_WARNING, INTERRUPTED, INVESTIGATING, EXPLAINED, DEFECTIVE);
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
