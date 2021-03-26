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
 * When a new validation run is created with a Passed status, or when a promotion is granted, we check all auto promoted promotion levels
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
        } else if (event.getEventType() == EventFactory.NEW_PROMOTION_RUN) {
            onNewPromotionRun(event);
        } else if (event.getEventType() == EventFactory.DELETE_PROMOTION_LEVEL) {
            onDeletePromotionLevel(event);
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
        promotionLevels.forEach(promotionLevel -> cleanPromotionLevelFromValidationStamp(promotionLevel, validationStampId));
    }

    private void onDeletePromotionLevel(Event event) {
        // Gets the promotion level ID
        int promotionLevelId = event.getIntValue("promotion_level_id");
        // Branch
        Branch branch = event.getEntity(ProjectEntityType.BRANCH);
        // Gets all promotion levels for this branch
        List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branch.getId());
        // Checks all promotion levels
        promotionLevels.forEach(promotionLevel -> cleanPromotionLevelFromPromotionLevel(promotionLevel, promotionLevelId));
    }

    private void cleanPromotionLevelFromValidationStamp(PromotionLevel promotionLevel, int validationStampId) {
        Optional<AutoPromotionProperty> oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType.class).option();
        if (oProperty.isPresent()) {
            AutoPromotionProperty property = oProperty.get();
            List<ValidationStamp> keptValidationStamps = property.getValidationStamps().stream().filter(
                    validationStamp -> (validationStampId != validationStamp.id())
            ).collect(Collectors.toList());
            if (keptValidationStamps.size() < property.getValidationStamps().size()) {
                AutoPromotionProperty editedProperty = new AutoPromotionProperty(
                        keptValidationStamps,
                        property.getInclude(),
                        property.getExclude(),
                        property.getPromotionLevels()
                );
                securityService.asAdmin(() -> {
                    propertyService.editProperty(
                            promotionLevel,
                            AutoPromotionPropertyType.class,
                            editedProperty
                    );
                });
            }
        }
    }

    private void cleanPromotionLevelFromPromotionLevel(PromotionLevel promotionLevel, int promotionLevelId) {
        Optional<AutoPromotionProperty> oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType.class).option();
        if (oProperty.isPresent()) {
            AutoPromotionProperty property = oProperty.get();
            List<PromotionLevel> keptPromotionLevels = property.getPromotionLevels().stream().filter(
                    pl -> (promotionLevelId != pl.id())
            ).collect(Collectors.toList());
            if (keptPromotionLevels.size() < property.getPromotionLevels().size()) {
                AutoPromotionProperty editedProperty = new AutoPromotionProperty(
                        property.getValidationStamps(),
                        property.getInclude(),
                        property.getExclude(),
                        keptPromotionLevels
                );
                securityService.asAdmin(() -> {
                    propertyService.editProperty(
                            promotionLevel,
                            AutoPromotionPropertyType.class,
                            editedProperty
                    );
                });
            }
        }
    }

    private void onNewValidationRun(Event event) {
        // Passed validation?
        ValidationRun validationRun = event.getEntity(ProjectEntityType.VALIDATION_RUN);
        if (Objects.equals(
                validationRun.getLastStatus().getStatusID(),
                ValidationRunStatusID.STATUS_PASSED)) {
            processEvent(event);
        }
    }

    private void onNewPromotionRun(Event event) {
        processEvent(event);
    }

    private void processEvent(Event event) {
        // Branch
        Branch branch = event.getEntity(ProjectEntityType.BRANCH);
        // Build
        Build build = event.getEntity(ProjectEntityType.BUILD);
        // Gets all promotion levels for this branch
        List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branch.getId());
        // Gets all validation stamps for this branch
        List<ValidationStamp> validationStamps = structureService.getValidationStampListForBranch(branch.getId());
        // Gets the promotion levels which have an auto promotion property
        promotionLevels.forEach(promotionLevel -> checkPromotionLevel(build, promotionLevel, promotionLevels, validationStamps));
    }

    private void checkPromotionLevel(Build build, PromotionLevel promotionLevel, List<PromotionLevel> promotionLevels, List<ValidationStamp> validationStamps) {
        Optional<AutoPromotionProperty> oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType.class).option();
        if (oProperty.isPresent()) {
            AutoPromotionProperty property = oProperty.get();
            // Checks if the property is eligible
            if (property.isEmpty()) {
                return;
            }
            // Check to be done only if the promotion level is not attributed yet
            List<PromotionRun> runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel);
            if (runs.isEmpty()) {
                // Checks the status of each validation stamp
                boolean allVSPassed = validationStamps.stream()
                        // Keeps only the ones selectable for the autopromotion property
                        .filter(property::contains)
                        // They must all pass
                        .allMatch(validationStamp -> isValidationStampPassed(build, validationStamp));
                // Checks that all needed promotions are granted
                boolean allPLPassed = promotionLevels.stream()
                        // Keeps only the ones selectable for the autopromotion property
                        .filter(property::contains)
                        // They must all be granted
                        .allMatch(pl -> isPromotionLevelGranted(build, pl));
                // Promotion is needed
                if (allVSPassed && allPLPassed) {
                    // Promotes
                    // Makes sure to raise the auth level because the one
                    // having made a validation might not be granted to
                    // creation a promotion
                    securityService.asAdmin(() ->
                            structureService.newPromotionRun(
                                    PromotionRun.of(
                                            build,
                                            promotionLevel,
                                            securityService.getCurrentSignature(),
                                            "Auto promotion"
                                    )
                            )
                    );
                }
            }
        }
    }

    private boolean isValidationStampPassed(Build build, ValidationStamp validationStamp) {
        List<ValidationRun> runs = structureService.getValidationRunsForBuildAndValidationStamp(
                build.getId(),
                validationStamp.getId(),
                0,
                1
        );
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

    private boolean isPromotionLevelGranted(Build build, PromotionLevel promotionLevel) {
        List<PromotionRun> runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel);
        return !runs.isEmpty();
    }

}
