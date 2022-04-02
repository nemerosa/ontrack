package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.controller.AbstractURIBuilder;
import net.nemerosa.ontrack.ui.controller.EntityURIBuilder;

import java.net.URI;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public abstract class AbstractEntityURIBuilder extends AbstractURIBuilder implements EntityURIBuilder {

    @Override
    public URI getEntityURI(ProjectEntity entity) {
        ProjectEntityType projectEntityType = entity.getProjectEntityType();
        switch (projectEntityType) {
            case PROJECT:
                return build(on(ProjectController.class).getProject(entity.getId()));
            case BRANCH:
                return build(on(BranchController.class).getBranch(entity.getId()));
            case PROMOTION_LEVEL:
                return build(on(PromotionLevelController.class).getPromotionLevel(entity.getId()));
            case VALIDATION_STAMP:
                return build(on(ValidationStampController.class).getValidationStamp(entity.getId()));
            case BUILD:
                return build(on(BuildController.class).getBuild(entity.getId()));
            case PROMOTION_RUN:
                return build(on(PromotionRunController.class).getPromotionRun(entity.getId()));
            case VALIDATION_RUN:
                return build(on(ValidationRunController.class).getValidationRun(entity.getId()));
            default:
                throw new IllegalStateException("Unknown entity type: " + projectEntityType);
        }
    }

}
