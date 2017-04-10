package net.nemerosa.ontrack.boot.resources;

import com.google.common.collect.Iterables;
import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BranchType;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.nemerosa.ontrack.ui.resource.LinkDefinitions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class BranchResourceDecorator extends AbstractLinkResourceDecorator<Branch> {

    private final ResourceDecorationContributorService resourceDecorationContributorService;
    private final StructureService structureService;

    @Autowired
    public BranchResourceDecorator(ResourceDecorationContributorService resourceDecorationContributorService, StructureService structureService) {
        super(Branch.class);
        this.resourceDecorationContributorService = resourceDecorationContributorService;
        this.structureService = structureService;
    }

    @Override
    protected Iterable<LinkDefinition<Branch>> getLinkDefinitions() {
        return Iterables.concat(
                Arrays.asList(
                        link(Link.SELF, branch -> on(BranchController.class).getBranch(branch.getId())),
                        link(
                                "_project",
                                branch -> on(ProjectController.class).getProject(branch.getProject().getId())),
                        // Build creation
                        link(
                                "_createBuild",
                                (Branch branch) -> on(BuildController.class).newBuild(branch.getId(), null),
                                (branch, resourceContext) -> branch.getType() != BranchType.TEMPLATE_DEFINITION &&
                                        resourceContext.isProjectFunctionGranted(branch, BuildCreate.class)
                        ),
                        // Promotion level creation
                        link(
                                "_createPromotionLevel",
                                branch -> on(PromotionLevelController.class).newPromotionLevelForm(branch.getId()),
                                withProjectFn(PromotionLevelCreate.class)
                        ),
                        // Promotion level list
                        link(
                                "_promotionLevels",
                                branch -> on(PromotionLevelController.class).getPromotionLevelListForBranch(branch.getId())
                        ),
                        // Validation stamp creation
                        link(
                                "_createValidationStamp",
                                branch -> on(ValidationStampController.class).newValidationStampForm(branch.getId()),
                                withProjectFn(ValidationStampCreate.class)
                        ),
                        // Validation stamp list
                        link(
                                "_validationStamps",
                                branch -> on(ValidationStampController.class).getValidationStampListForBranch(branch.getId())
                        ),
                        // Validation stamp list with view
                        link(
                                "_validationStampViews",
                                branch -> on(ValidationStampController.class).getValidationStampViewListForBranch(branch.getId())
                        ),
                        // All branches for the same project
                        link(
                                "_branches",
                                branch -> on(BranchController.class).getBranchListForProject(branch.getProjectId())
                        ),
                        // Actual properties for this build
                        link("_properties", branch -> on(PropertyController.class).getProperties(ProjectEntityType.BRANCH, branch.getId())),
                        // Actions
                        link("_actions", branch -> on(ProjectEntityExtensionController.class).getActions(ProjectEntityType.BRANCH, branch.getId())),
                        // Update link (with authorisation)
                        link(
                                Link.UPDATE,
                                branch -> on(BranchController.class).getUpdateForm(branch.getId()),
                                withProjectFn(BranchEdit.class)
                        ),
                        // Bulk update
                        link(
                                "_bulkUpdate",
                                branch -> on(BranchController.class).bulkUpdate(branch.getId()),
                                withProjectFn(BranchEdit.class)
                        ),
                        // Delete link
                        link(
                                Link.DELETE,
                                branch -> on(BranchController.class).deleteBranch(branch.getId()),
                                withProjectFn(BranchDelete.class)
                        ),
                        // View link
                        link(
                                "_status",
                                branch -> on(BranchController.class).getBranchStatusView(branch.getId())
                        ),
                        // Builds link
                        link(
                                "_view",
                                branch -> on(BranchController.class).buildView(branch.getId())
                        ),
                        // Decorations
                        link(
                                "_decorations",
                                branch -> on(DecorationsController.class).getDecorations(branch.getProjectEntityType(), branch.getId())
                        ),
                        // Build filters
                        link(
                                "_buildFilterResources",
                                branch -> on(BuildFilterController.class).buildFilters(branch.getId())
                        ),
                        // Build filter forms
                        link(
                                "_buildFilterForms",
                                branch -> on(BuildFilterController.class).buildFilterForms(branch.getId())
                        ),
                        // Saving a filter
                        link(
                                "_buildFilterSave",
                                branch -> on(BuildFilterController.class).createFilter(branch.getId(), null)
                        ),
                        // Sharing a filter
                        link(
                                "_buildFilterShare",
                                branch -> on(BuildFilterController.class).createFilter(branch.getId(), null),
                                withProjectFn(BranchFilterMgt.class)
                        ),
                        // Reordering of promotion levels
                        link(
                                "_reorderPromotionLevels",
                                branch -> on(PromotionLevelController.class).reorderPromotionLevelListForBranch(branch.getId(), null),
                                withProjectFn(PromotionLevelEdit.class)
                        ),
                        // Reordering of validation stamps
                        link(
                                "_reorderValidationStamps",
                                branch -> on(ValidationStampController.class).reorderValidationStampListForBranch(branch.getId(), null),
                                withProjectFn(ValidationStampEdit.class)
                        ),
                        // Copy of a configuration from another branch
                        link(
                                "_copy",
                                branch -> on(BranchController.class).copy(branch.getId()),
                                withProjectFn(BranchEdit.class)
                        ),
                        // Clone to another branch
                        link(
                                "_clone",
                                branch -> on(BranchController.class).clone(branch.getId()),
                                withProjectFn(BranchCreate.class)
                        ),
                        // Events
                        link(
                                "_events",
                                branch -> on(EventController.class).getEvents(branch.getProjectEntityType(), branch.getId(), 0, 10)
                        ),
                        // Enable
                        link(
                                "_enable",
                                (Branch branch) -> on(BranchController.class).enableBranch(branch.getId()),
                                (branch, resourceContext) -> resourceContext.isProjectFunctionGranted(branch.projectId(), BranchEdit.class)
                                        && branch.isDisabled()
                                        && branch.getType() != BranchType.TEMPLATE_DEFINITION
                        ),
                        // Disable
                        link(
                                "_disable",
                                (Branch branch) -> on(BranchController.class).disableBranch(branch.getId()),
                                (branch, resourceContext) -> resourceContext.isProjectFunctionGranted(branch.projectId(), BranchEdit.class)
                                        && !branch.isDisabled()
                                        && branch.getType() != BranchType.TEMPLATE_DEFINITION
                        ),
                        // Template definition creation
                        link(
                                "_templateDefinition",
                                (Branch branch) -> on(BranchController.class).getTemplateDefinition(branch.getId()),
                                (branch, resourceContext) -> branch.getType() != BranchType.TEMPLATE_INSTANCE
                                        && (structureService.getBuildCount(branch) == 0)
                                        && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                        ),
                        // Template synchronisation
                        link(
                                "_templateSync",
                                (Branch branch) -> on(BranchController.class).syncTemplateDefinition(branch.getId()),
                                (branch, resourceContext) -> branch.getType() == BranchType.TEMPLATE_DEFINITION
                                        && (resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                                        || resourceContext.isProjectFunctionGranted(branch, BranchTemplateSync.class)
                                )
                        ),
                        // Template instance creation
                        link(
                                "_templateInstanceCreate",
                                (Branch branch) -> on(BranchController.class).singleTemplateInstanceForm(branch.getId()),
                                (branch, resourceContext) -> branch.getType() == BranchType.TEMPLATE_DEFINITION
                                        && (resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                                        || resourceContext.isProjectFunctionGranted(branch, BranchTemplateSync.class)
                                )
                        ),
                        // Template instance
                        link(
                                "_templateInstance",
                                (Branch branch) -> on(BranchController.class).getTemplateInstance(branch.getId()),
                                (branch, resourceContext) -> branch.getType() == BranchType.TEMPLATE_INSTANCE
                                        && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                        ),
                        // Template instance disconnection
                        link(
                                "_templateInstanceDisconnect",
                                (Branch branch) -> on(BranchController.class).disconnectTemplateInstance(branch.getId()),
                                (branch, resourceContext) -> branch.getType() == BranchType.TEMPLATE_INSTANCE
                                        && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                        ),
                        // Template instance connection
                        link(
                                "_templateInstanceConnect",
                                (Branch branch) -> on(BranchController.class).connectTemplateInstance(branch.getId()),
                                (branch, resourceContext) -> branch.getType() == BranchType.CLASSIC
                                        && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                        ),
                        // Template instance synchronisation
                        link(
                                "_templateInstanceSync",
                                (Branch branch) -> on(BranchController.class).syncTemplateInstance(branch.getId()),
                                (branch, resourceContext) -> branch.getType() == BranchType.TEMPLATE_INSTANCE
                                        && (resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt.class)
                                        || resourceContext.isProjectFunctionGranted(branch, BranchTemplateSync.class)
                                )
                        ),
                        // Page
                        page()
                ),
                // Contributions
                resourceDecorationContributorService.getLinkDefinitions(ProjectEntityType.BRANCH)
        );
    }

}
