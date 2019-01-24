package net.nemerosa.ontrack.boot.ui;

import com.google.common.base.CaseFormat;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.lang.String.format;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public abstract class AbstractURIBuilder implements URIBuilder {

    protected String pagePath(String path, Object... arguments) {
        return format(
                "/#/%s",
                format(path, arguments)
        );
    }

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

    String getEntityPageName(ProjectEntityType projectEntityType) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, projectEntityType.name().toLowerCase());
    }

    @Override
    public URI getEntityPage(ProjectEntity entity) {
        return page(
                "%s/%d",
                getEntityPageName(entity.getProjectEntityType()),
                entity.id()
        );
    }
}
