package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            // Passed validation?
            ValidationRun validationRun = (ValidationRun) event.getEntities().get(ProjectEntityType.VALIDATION_RUN);
            Validate.notNull(validationRun, "Validation run entity required on the new validation run event");
            if (Objects.equals(
                    validationRun.getLastStatus().getStatusID(),
                    ValidationRunStatusID.STATUS_PASSED)) {
                // Branch
                Branch branch = (Branch) event.getEntities().get(ProjectEntityType.BRANCH);
                Validate.notNull(branch, "Branch entity required on the new validation run event");
                // Build
                Build build = (Build) event.getEntities().get(ProjectEntityType.BUILD);
                Validate.notNull(build, "Build entity required on the new validation run event");
                // Gets all promotion levels for this branch
                List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branch.getId());
                // Gets the promotion levels which have an auto promotion property
                promotionLevels.forEach(promotionLevel -> checkPromotionLevel(build, promotionLevel));
            }
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
