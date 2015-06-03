package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * When a new validation run is created with a Passed status, we check all auto promoted promotion levels
 * to know if each of their validation stamps is now passed.
 */
@Component
public class AutoPromotionEventListener implements EventListener {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public AutoPromotionEventListener(StructureService structureService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getEventType() == EventFactory.NEW_VALIDATION_RUN) {
            onNewValidationRun(event);
        } else if (event.getEventType() == EventFactory.DELETE_VALIDATION_STAMP) {
            onDeleteValidationStamp(event);
        }
    }

    private void onDeleteValidationStamp(Event event) {
        // Gets the validation stamp ID
        int validationStampId = event.getIntValue("validation_stamp_id");
        // Branch
        Branch branch = event.getEntity(ProjectEntityType.BRANCH);
        // Gets all promotion levels for this branch
        List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branch.getId());
        // Checks all promotion levels
        promotionLevels.forEach(promotionLevel -> cleanPromotionLevel(promotionLevel, validationStampId));
    }

    private void cleanPromotionLevel(PromotionLevel promotionLevel, int validationStampId) {
        Optional<AutoPromotionProperty> oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType.class).option();
        if (oProperty.isPresent()) {
            AutoPromotionProperty property = oProperty.get();
            List<ValidationStamp> keptValidationStamps = property.getValidationStamps().stream().filter(
                    validationStamp -> (validationStampId != validationStamp.id())
            ).collect(Collectors.toList());
            if (keptValidationStamps.size() < property.getValidationStamps().size()) {
                property = new AutoPromotionProperty(
                        keptValidationStamps
                );
                propertyService.editProperty(
                        promotionLevel,
                        AutoPromotionPropertyType.class,
                        property
                );
            }
        }
    }

    private void onNewValidationRun(Event event) {
        // Passed validation?
        ValidationRun validationRun = event.getEntity(ProjectEntityType.VALIDATION_RUN);
        if (Objects.equals(
                validationRun.getLastStatus().getStatusID(),
                ValidationRunStatusID.STATUS_PASSED)) {
            // Branch
            Branch branch = event.getEntity(ProjectEntityType.BRANCH);
            // Build
            Build build = event.getEntity(ProjectEntityType.BUILD);
            // Gets all promotion levels for this branch
            List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branch.getId());
            // Gets the promotion levels which have an auto promotion property
            promotionLevels.forEach(promotionLevel -> checkPromotionLevel(build, promotionLevel));
        }
    }

    protected void checkPromotionLevel(Build build, PromotionLevel promotionLevel) {
        Optional<AutoPromotionProperty> oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType.class).option();
        if (oProperty.isPresent()) {
            AutoPromotionProperty property = oProperty.get();
            // Checks the status of each validation stamp
            boolean allPassed = property.getValidationStamps().stream().allMatch(validationStamp -> isPassed(build, validationStamp));
            if (allPassed) {
                // Promotes
                structureService.newPromotionRun(
                        PromotionRun.of(
                                build,
                                promotionLevel,
                                securityService.getCurrentSignature(),
                                "Auto promotion"
                        )
                );
            }
        }
    }

    protected boolean isPassed(Build build, ValidationStamp validationStamp) {
        List<ValidationRun> runs = structureService.getValidationRunsForBuildAndValidationStamp(build.getId(), validationStamp.getId());
        if (runs.isEmpty()) {
            return false;
        } else {
            ValidationRun run = runs.get(0);
            return Objects.equals(
                    run.getLastStatus().getStatusID(),
                    ValidationRunStatusID.STATUS_PASSED
            );
        }
    }

}
