package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.PromotionLevelController;
import net.nemerosa.ontrack.boot.ui.PromotionRunController;
import net.nemerosa.ontrack.boot.ui.PropertyController;
import net.nemerosa.ontrack.model.security.PromotionRunDelete;
import net.nemerosa.ontrack.model.structure.PromotionRun;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.nemerosa.ontrack.ui.resource.LinkDefinitions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class PromotionRunResourceDecorator extends AbstractLinkResourceDecorator<PromotionRun> {

    public PromotionRunResourceDecorator() {
        super(PromotionRun.class);
    }

    @Override
    protected Iterable<LinkDefinition<PromotionRun>> getLinkDefinitions() {
        return Arrays.asList(
                // Self
                self(promotionRun -> on(PromotionRunController.class).getPromotionRun(promotionRun.getId())),
                // List of runs for the build and promotion level
                link(
                        "_all",
                        promotionRun -> on(PromotionRunController.class).getPromotionRunsForBuildAndPromotionLevel(
                                promotionRun.getBuild().getId(),
                                promotionRun.getPromotionLevel().getId()
                        )
                ),
                // Deletion
                delete(
                        promotionRun -> on(PromotionRunController.class).deletePromotionRun(promotionRun.getId()),
                        PromotionRunDelete.class
                ),
                // Actual properties for this item
                link(
                        "_properties",
                        promotionRun -> on(PropertyController.class).getProperties(promotionRun.getProjectEntityType(), promotionRun.getId())
                ),
                // Image
                link(
                        Link.IMAGE_LINK,
                        promotionRun -> on(PromotionLevelController.class).getPromotionLevelImage_(
                                null,
                                promotionRun.getPromotionLevel().getId()
                        )
                ),
                // Page
                page()
        );
    }

}
