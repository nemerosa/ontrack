package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.PromotionLevelController;
import net.nemerosa.ontrack.boot.ui.PromotionRunController;
import net.nemerosa.ontrack.boot.ui.PropertyController;
import net.nemerosa.ontrack.model.security.PromotionRunDelete;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PromotionRun;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class PromotionRunResourceDecorator extends AbstractResourceDecorator<PromotionRun> {

    protected PromotionRunResourceDecorator() {
        super(PromotionRun.class);
    }

    @Override
    public List<Link> links(PromotionRun promotionRun, ResourceContext resourceContext) {
        return resourceContext.links()
                // Self
                .self(on(PromotionRunController.class).getPromotionRun(promotionRun.getId()))
                        // List of runs for the build and promotion level
                .link(
                        "_all",
                        on(PromotionRunController.class).getPromotionRunsForBuildAndPromotionLevel(
                                promotionRun.getBuild().getId(),
                                promotionRun.getPromotionLevel().getId()
                        )
                )
                        // Deletion
                .delete(on(PromotionRunController.class).deletePromotionRun(promotionRun.getId()), PromotionRunDelete.class, promotionRun.projectId())
                        // Actual properties for this item
                .link("_properties", on(PropertyController.class).getProperties(ProjectEntityType.PROMOTION_RUN, promotionRun.getId()))
                        // Image
                .link(
                        Link.IMAGE_LINK,
                        on(PromotionLevelController.class).getPromotionLevelImage_(
                                promotionRun.getPromotionLevel().getId()
                        )
                )
                        // OK
                .build();
    }

}
