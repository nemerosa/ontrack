package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.PromotionLevelDelete;
import net.nemerosa.ontrack.model.security.PromotionLevelEdit;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.nemerosa.ontrack.ui.resource.LinkDefinitions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class PromotionLevelResourceDecorator extends AbstractLinkResourceDecorator<PromotionLevel> {

    public PromotionLevelResourceDecorator() {
        super(PromotionLevel.class);
    }

    @Override
    protected Iterable<LinkDefinition<PromotionLevel>> getLinkDefinitions() {
        return Arrays.asList(
                link(
                        Link.SELF,
                        promotionLevel -> on(PromotionLevelController.class).getPromotionLevel(promotionLevel.getId())
                ),
                link(
                        "_branch",
                        promotionLevel -> on(BranchController.class).getBranch(promotionLevel.getBranch().getId())
                ),
                link(
                        "_project",
                        promotionLevel -> on(ProjectController.class).getProject(promotionLevel.getBranch().getProject().getId())
                ),
                link(
                        Link.IMAGE_LINK,
                        promotionLevel -> on(PromotionLevelController.class).getPromotionLevelImage_(null, promotionLevel.getId())
                ),
                // Update
                link(
                        Link.UPDATE,
                        promotionLevel -> on(PromotionLevelController.class).updatePromotionLevelForm(promotionLevel.getId()),
                        withProjectFn(PromotionLevelEdit.class)
                ),
                // Delete
                link(
                        Link.DELETE,
                        promotionLevel -> on(PromotionLevelController.class).deletePromotionLevel(promotionLevel.getId()),
                        withProjectFn(PromotionLevelDelete.class)
                ),
                // TODO Next promotion level
                // TODO Previous promotion level
                // Decorations
                link(
                        "_decorations",
                        promotionLevel -> on(DecorationsController.class).getDecorations(promotionLevel.getProjectEntityType(), promotionLevel.getId())
                ),
                // Actual properties for this item
                link(
                        "_properties",
                        promotionLevel -> on(PropertyController.class).getProperties(promotionLevel.getProjectEntityType(), promotionLevel.getId())
                ),
                // Promotion runs
                link(
                        "_runs",
                        promotionLevel -> on(PromotionLevelController.class).getPromotionRunView(promotionLevel.getId())
                ),
                // Events
                link(
                        "_events",
                        promotionLevel -> on(EventController.class).getEvents(promotionLevel.getProjectEntityType(), promotionLevel.getId(), 0, 10)
                ),
                // Page
                page()
        );
    }

}
