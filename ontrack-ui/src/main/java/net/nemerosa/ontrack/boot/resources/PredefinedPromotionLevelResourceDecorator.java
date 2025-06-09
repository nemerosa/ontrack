package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.PredefinedPromotionLevelController;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class PredefinedPromotionLevelResourceDecorator extends AbstractResourceDecorator<PredefinedPromotionLevel> {

    public PredefinedPromotionLevelResourceDecorator() {
        super(PredefinedPromotionLevel.class);
    }

    @Override
    public List<Link> links(PredefinedPromotionLevel promotionLevel, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(PredefinedPromotionLevelController.class).getPromotionLevel(promotionLevel.getId()))
                        // Image link
                .link(Link.IMAGE_LINK, on(PredefinedPromotionLevelController.class).getPromotionLevelImage(promotionLevel.getId()))
                        // Delete link
                .link(Link.DELETE, on(PredefinedPromotionLevelController.class).deletePromotionLevel(promotionLevel.getId()), GlobalSettings.class)
                        // OK
                .build();
    }

}
