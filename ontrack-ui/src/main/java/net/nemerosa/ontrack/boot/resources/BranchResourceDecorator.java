package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.PromotionLevelCreate;
import net.nemerosa.ontrack.model.security.ValidationStampCreate;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class BranchResourceDecorator extends AbstractResourceDecorator<Branch> {

    protected BranchResourceDecorator() {
        super(Branch.class);
    }

    @Override
    public List<Link> links(Branch branch, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(BranchController.class).getBranch(branch.getId()))
                .link(
                        "_project",
                        on(ProjectController.class).getProject(branch.getProject().getId()))
                        // Build creation
                .link(
                        "_createBuild",
                        on(BuildController.class).newBuild(branch.getId(), null),
                        BuildCreate.class, branch.getProject().id())
                        // Promotion level creation
                .link(
                        "_createPromotionLevel",
                        on(PromotionLevelController.class).newPromotionLevelForm(branch.getId()),
                        PromotionLevelCreate.class, branch.getProject().id()
                )
                        // Promotion level list
                .link(
                        "_promotionLevels",
                        on(PromotionLevelController.class).getPromotionLevelListForBranch(branch.getId())
                )
                        // Validation stamp creation
                .link(
                        "_createValidationStamp",
                        on(ValidationStampController.class).newValidationStampForm(branch.getId()),
                        ValidationStampCreate.class, branch.getProject().id()
                )
                        // Validation stamp list
                .link(
                        "_validationStamps",
                        on(ValidationStampController.class).getValidationStampListForBranch(branch.getId())
                )
                        // Actual properties for this build
                .link("_properties", on(PropertyController.class).getProperties(ProjectEntityType.BRANCH, branch.getId()))
                        // TODO Update link (with authorisation)
                        // TODO Delete link
                        // View link
                .link("_status", on(BranchController.class).getBranchStatusView(branch.getId()))
                        // TODO Builds link
                        // OK
                .build();
    }

}
