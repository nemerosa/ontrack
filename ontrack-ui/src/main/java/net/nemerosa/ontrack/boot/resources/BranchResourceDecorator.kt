package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.*
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.resource.*
import net.nemerosa.ontrack.ui.resource.LinkDefinitions.page
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class BranchResourceDecorator(
        private val resourceDecorationContributorService: ResourceDecorationContributorService,
        private val branchFavouriteService: BranchFavouriteService
) : AbstractLinkResourceDecorator<Branch>(Branch::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<Branch>> {
        return listOf(
                Link.SELF.linkTo { branch -> on(BranchController::class.java).getBranch(branch.id) },
                "_project".linkTo { branch -> on(ProjectController::class.java).getProject(branch.project.id) },
                // Build creation
                "_createBuild"
                        linkTo { branch: Branch -> on(BuildController::class.java).newBuild(branch.id, null) }
                        linkIf { branch, resourceContext -> resourceContext.isProjectFunctionGranted(branch, BuildCreate::class.java) }
                ,
                // Promotion level creation
                "_createPromotionLevel" linkTo { branch: Branch ->
                    on(PromotionLevelController::class.java).newPromotionLevelForm(branch.id)
                } linkIf PromotionLevelCreate::class,
                // Promotion level list
                "_promotionLevels" linkTo { branch -> on(PromotionLevelController::class.java).getPromotionLevelListForBranch(branch.id) },
                // Validation stamp creation
                "_createValidationStamp" linkTo { branch: Branch ->
                    on(ValidationStampController::class.java).newValidationStampForm(branch.id)
                } linkIf ValidationStampCreate::class,
                // Validation stamp list
                "_validationStamps" linkTo { branch -> on(ValidationStampController::class.java).getValidationStampListForBranch(branch.id) },
                // Validation stamp list with view
                "_validationStampViews" linkTo { branch -> on(ValidationStampController::class.java).getValidationStampViewListForBranch(branch.id) },
                // List of validation stamp filters for this branch, all of them
                "_allValidationStampFilters" linkTo { branch -> on(ValidationStampFilterController::class.java).getAllBranchValidationStampFilters(branch.id) },
                // Creation of a validation stamp filter for the branch
                "_validationStampFilterCreate" linkTo { branch: Branch ->
                    on(ValidationStampFilterController::class.java).getBranchValidationStampFilterForm(branch.id)
                } linkIf ValidationStampFilterCreate::class,
                // All branches for the same project
                "_branches" linkTo { branch -> on(BranchController::class.java).getBranchListForProject(branch.projectId) },
                // Actual properties for this build
                "_properties" linkTo { branch -> on(PropertyController::class.java).getProperties(ProjectEntityType.BRANCH, branch.id) },
                // Extra information
                "_extra" linkTo { branch ->
                    on(ProjectEntityExtensionController::class.java).getInformation(ProjectEntityType.BRANCH, branch.id)
                },
                // Actions
                "_actions" linkTo { branch -> on(ProjectEntityExtensionController::class.java).getActions(ProjectEntityType.BRANCH, branch.id) },
                // Update link (with authorisation)
                Link.UPDATE linkTo { branch: Branch ->
                    on(BranchController::class.java).getUpdateForm(branch.id)
                } linkIf BranchEdit::class,
                // Bulk update
                "_bulkUpdate" linkTo { branch: Branch ->
                    on(BranchController::class.java).bulkUpdate(branch.id)
                } linkIf BranchEdit::class,
                // Delete link
                Link.DELETE linkTo { branch: Branch ->
                    on(BranchController::class.java).deleteBranch(branch.id)
                } linkIf BranchDelete::class,
                // View link
                "_status" linkTo { branch -> on(BranchController::class.java).getBranchStatusView(branch.id) },
                // Builds link
                "_view" linkTo { branch -> on(BranchController::class.java).buildView(branch.id) },
                // Decorations
                "_decorations" linkTo { branch -> on(DecorationsController::class.java).getDecorations(branch.projectEntityType, branch.id) },
                // Build filters
                "_buildFilterResources" linkTo { branch -> on(BuildFilterController::class.java).buildFilters(branch.id) },
                // Build filter forms
                "_buildFilterForms" linkTo { branch -> on(BuildFilterController::class.java).buildFilterForms(branch.id) },
                // Saving a filter
                "_buildFilterSave" linkTo { branch -> on(BuildFilterController::class.java).createFilter(branch.id, null) },
                // Sharing a filter
                "_buildFilterShare" linkTo { branch: Branch ->
                    on(BuildFilterController::class.java).createFilter(branch.id, null)
                } linkIf BranchFilterMgt::class,
                // Reordering of promotion levels
                "_reorderPromotionLevels" linkTo { branch: Branch ->
                    on(PromotionLevelController::class.java).reorderPromotionLevelListForBranch(branch.id, null)
                } linkIf PromotionLevelEdit::class,
                // Reordering of validation stamps
                "_reorderValidationStamps" linkTo { branch: Branch ->
                    on(ValidationStampController::class.java).reorderValidationStampListForBranch(branch.id, null)
                } linkIf ValidationStampEdit::class,
                // Copy of a configuration from another branch
                "_copy" linkTo { branch: Branch ->
                    on(BranchController::class.java).copy(branch.id)
                } linkIf (BranchEdit::class),
                // Clone to another branch
                "_clone" linkTo { branch: Branch ->
                    on(BranchController::class.java).clone(branch.id)
                } linkIf (BranchCreate::class),
                // Events
                "_events" linkTo { branch -> on(EventController::class.java).getEvents(branch.projectEntityType, branch.id, 0, 10) },
                // Enable
                "_enable" linkTo { branch: Branch -> on(BranchController::class.java).enableBranch(branch.id) }
                        linkIf { branch, resourceContext ->
                    (resourceContext.isProjectFunctionGranted(branch.projectId(), BranchEdit::class.java)
                            && branch.isDisabled)
                },
                // Disable
                "_disable" linkTo { branch: Branch -> on(BranchController::class.java).disableBranch(branch.id) }
                        linkIf { branch, resourceContext ->
                    (resourceContext.isProjectFunctionGranted(branch.projectId(), BranchEdit::class.java)
                            && !branch.isDisabled)
                },
                // Favourite --> 'unfavourite'
                "_unfavourite" linkTo { branch: Branch -> on(BranchController::class.java).unfavouriteBranch(branch.id) }
                        linkIf { branch, resourceContext -> resourceContext.isLogged && branchFavouriteService.isBranchFavourite(branch) }
                ,
                // Not favourite --> 'favourite'
                "_favourite" linkTo { branch: Branch -> on(BranchController::class.java).favouriteBranch(branch.id) }
                        linkIf { branch, resourceContext -> resourceContext.isLogged && !branchFavouriteService.isBranchFavourite(branch) }
                ,
                // Page
                page()
        ) + resourceDecorationContributorService.getLinkDefinitions(ProjectEntityType.BRANCH)
    }

}
