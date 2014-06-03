package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.boot.ui.PromotionLevelController;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class PromotionLevelResourceDecorator extends AbstractResourceDecorator<PromotionLevel> {

    protected PromotionLevelResourceDecorator() {
        super(PromotionLevel.class);
    }

    @Override
    public List<Link> links(PromotionLevel promotionLevel, ResourceContext resourceContext) {
        return Arrays.asList(
                Link.of(Link.SELF, resourceContext.uri(on(PromotionLevelController.class).getPromotionLevel(promotionLevel.getId()))),
                Link.of("_branchLink", resourceContext.uri(on(BranchController.class).getBranch(promotionLevel.getBranch().getId()))),
                Link.of("_projectLink", resourceContext.uri(on(ProjectController.class).getProject(promotionLevel.getBranch().getProject().getId()))),
                Link.of(Link.IMAGE_LINK, resourceContext.uri(on(PromotionLevelController.class).getPromotionLevelImage_(promotionLevel.getId())))
                // TODO Actions?
        );
    }

}
