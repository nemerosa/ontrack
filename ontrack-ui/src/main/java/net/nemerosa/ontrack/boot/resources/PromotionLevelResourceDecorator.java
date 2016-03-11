package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.PromotionLevelDelete;
import net.nemerosa.ontrack.model.security.PromotionLevelEdit;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class PromotionLevelResourceDecorator extends AbstractResourceDecorator<PromotionLevel> {

    public PromotionLevelResourceDecorator() {
        super(PromotionLevel.class);
    }

    @Override
    public List<Link> links(PromotionLevel promotionLevel, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(PromotionLevelController.class).getPromotionLevel(promotionLevel.getId()))
                .link("_branch", on(BranchController.class).getBranch(promotionLevel.getBranch().getId()))
                .link("_project", on(ProjectController.class).getProject(promotionLevel.getBranch().getProject().getId()))
                .link(Link.IMAGE_LINK, on(PromotionLevelController.class).getPromotionLevelImage_(null, promotionLevel.getId()))
                // Update
                .update(on(PromotionLevelController.class).updatePromotionLevelForm(promotionLevel.getId()), PromotionLevelEdit.class, promotionLevel.projectId())
                // Delete
                .delete(on(PromotionLevelController.class).deletePromotionLevel(promotionLevel.getId()), PromotionLevelDelete.class, promotionLevel.projectId())
                // TODO Next promotion level
                // TODO Previous promotion level
                // Decorations
                .link("_decorations", on(DecorationsController.class).getDecorations(promotionLevel.getProjectEntityType(), promotionLevel.getId()))
                // Actual properties for this item
                .link("_properties", on(PropertyController.class).getProperties(promotionLevel.getProjectEntityType(), promotionLevel.getId()))
                // Promotion runs
                .link("_runs", on(PromotionLevelController.class).getPromotionRunView(promotionLevel.getId()))
                // Events
                .link("_events", on(EventController.class).getEvents(promotionLevel.getProjectEntityType(), promotionLevel.getId(), 0, 10))
                // Page
                .page(promotionLevel)
                // OK
                .build();
    }

}
