package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.*;
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
                        // Actions
                .link("_actions", on(ProjectEntityExtensionController.class).getActions(ProjectEntityType.BRANCH, branch.getId()))
                        // Update link (with authorisation)
                .update(on(BranchController.class).getUpdateForm(branch.getId()), BranchEdit.class, branch.projectId())
                        // Delete link
                .delete(on(BranchController.class).deleteBranch(branch.getId()), BranchDelete.class, branch.projectId())
                        // View link
                .link("_status", on(BranchController.class).getBranchStatusView(branch.getId()))
                        // Builds link
                .link("_view", on(BranchController.class).buildView(branch.getId()))
                        // Decorations
                .link("_decorations", on(DecorationsController.class).getDecorations(branch.getProjectEntityType(), branch.getId()))
                        // Build filters
                .link("_buildFilterResources", on(BuildFilterController.class).buildFilters(branch.getId()))
                        // Build filter forms
                .link("_buildFilterForms", on(BuildFilterController.class).buildFilterForms(branch.getId()))
                        // Saving a filter
                .link("_buildFilterSave", on(BuildFilterController.class).createFilter(branch.getId(), null))
                        // Reordering of promotion levels
                .link(
                        "_reorderPromotionLevels",
                        on(PromotionLevelController.class).reorderPromotionLevelListForBranch(branch.getId(), null),
                        PromotionLevelEdit.class,
                        branch.projectId()
                )
                        // Reordering of validation stamps
                .link(
                        "_reorderValidationStamps",
                        on(ValidationStampController.class).reorderValidationStampListForBranch(branch.getId(), null),
                        ValidationStampEdit.class,
                        branch.projectId()
                )
                        // OK
                .build();
    }

}
