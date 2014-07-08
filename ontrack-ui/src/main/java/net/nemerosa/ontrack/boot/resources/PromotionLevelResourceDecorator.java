package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.boot.ui.DecorationsController;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.boot.ui.PromotionLevelController;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class PromotionLevelResourceDecorator extends AbstractResourceDecorator<PromotionLevel> {

    protected PromotionLevelResourceDecorator() {
        super(PromotionLevel.class);
    }

    @Override
    public List<Link> links(PromotionLevel promotionLevel, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(PromotionLevelController.class).getPromotionLevel(promotionLevel.getId()))
                .link("_branch", on(BranchController.class).getBranch(promotionLevel.getBranch().getId()))
                .link("_project", on(ProjectController.class).getProject(promotionLevel.getBranch().getProject().getId()))
                .link(Link.IMAGE_LINK, on(PromotionLevelController.class).getPromotionLevelImage_(promotionLevel.getId()))
                        // TODO Update
                        // TODO Delete
                        // TODO Next promotion level
                        // TODO Previous promotion level
                        // Decorations
                .link("_decorations", on(DecorationsController.class).getDecorations(promotionLevel.getProjectEntityType(), promotionLevel.getId()))
                        // OK
                .build();
    }

}
