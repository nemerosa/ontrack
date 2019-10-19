package net.nemerosa.ontrack.boot.resources

import com.google.common.collect.Iterables
import net.nemerosa.ontrack.boot.ui.*
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.Arrays

import net.nemerosa.ontrack.ui.resource.LinkDefinitions.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class BranchResourceDecorator @Autowired
constructor(private val resourceDecorationContributorService: ResourceDecorationContributorService, private val structureService: StructureService, private val branchFavouriteService: BranchFavouriteService) : AbstractLinkResourceDecorator<Branch>(Branch::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<Branch>> {
        return listOf(
                link(Link.SELF) { branch -> on(BranchController::class.java).getBranch(branch.id) },
                link(
                        "_project"
                ) { branch -> on(ProjectController::class.java).getProject(branch.project.id) },
                // Build creation
                link(
                        "_createBuild",
                        { branch: Branch -> on(BuildController::class.java).newBuild(branch.id, null) },
                        { branch, resourceContext -> branch.type != BranchType.TEMPLATE_DEFINITION && resourceContext.isProjectFunctionGranted(branch, BuildCreate::class.java) }
                ),
                // Promotion level creation
                "_createPromotionLevel" linkTo { branch: Branch ->
                    on(PromotionLevelController::class.java).newPromotionLevelForm(branch.id)
                } linkIf PromotionLevelCreate::class,
                // Promotion level list
                link(
                        "_promotionLevels"
                ) { branch -> on(PromotionLevelController::class.java).getPromotionLevelListForBranch(branch.id) },
                // Validation stamp creation
                "_createValidationStamp" linkTo { branch: Branch ->
                    on(ValidationStampController::class.java).newValidationStampForm(branch.id)
                } linkIf ValidationStampCreate::class,
                // Validation stamp list
                link(
                        "_validationStamps"
                ) { branch -> on(ValidationStampController::class.java).getValidationStampListForBranch(branch.id) },
                // Validation stamp list with view
                link(
                        "_validationStampViews"
                ) { branch -> on(ValidationStampController::class.java).getValidationStampViewListForBranch(branch.id) },
                // List of validation stamp filters for this branch, all of them
                link(
                        "_allValidationStampFilters"
                ) { branch -> on(ValidationStampFilterController::class.java).getAllBranchValidationStampFilters(branch.id) },
                // Creation of a validation stamp filter for the branch
                "_validationStampFilterCreate" linkTo { branch: Branch ->
                    on(ValidationStampFilterController::class.java).getBranchValidationStampFilterForm(branch.id)
                } linkIf ValidationStampFilterCreate::class,
                // All branches for the same project
                link(
                        "_branches"
                ) { branch -> on(BranchController::class.java).getBranchListForProject(branch.projectId) },
                // Actual properties for this build
                link("_properties") { branch -> on(PropertyController::class.java).getProperties(ProjectEntityType.BRANCH, branch.id) },
                // Actions
                link("_actions") { branch -> on(ProjectEntityExtensionController::class.java).getActions(ProjectEntityType.BRANCH, branch.id) },
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
                link(
                        "_status"
                ) { branch -> on(BranchController::class.java).getBranchStatusView(branch.id) },
                // Builds link
                link(
                        "_view"
                ) { branch -> on(BranchController::class.java).buildView(branch.id) },
                // Decorations
                link(
                        "_decorations"
                ) { branch -> on(DecorationsController::class.java).getDecorations(branch.projectEntityType, branch.id) },
                // Build filters
                link(
                        "_buildFilterResources"
                ) { branch -> on(BuildFilterController::class.java).buildFilters(branch.id) },
                // Build filter forms
                link(
                        "_buildFilterForms"
                ) { branch -> on(BuildFilterController::class.java).buildFilterForms(branch.id) },
                // Saving a filter
                link(
                        "_buildFilterSave"
                ) { branch -> on(BuildFilterController::class.java).createFilter(branch.id, null) },
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
                link(
                        "_events"
                ) { branch -> on(EventController::class.java).getEvents(branch.projectEntityType, branch.id, 0, 10) },
                // Enable
                link(
                        "_enable",
                        { branch: Branch -> on(BranchController::class.java).enableBranch(branch.id) },
                        { branch, resourceContext ->
                            (resourceContext.isProjectFunctionGranted(branch.projectId(), BranchEdit::class.java)
                                    && branch.isDisabled
                                    && branch.type != BranchType.TEMPLATE_DEFINITION)
                        }
                ),
                // Disable
                link(
                        "_disable",
                        { branch: Branch -> on(BranchController::class.java).disableBranch(branch.id) },
                        { branch, resourceContext ->
                            (resourceContext.isProjectFunctionGranted(branch.projectId(), BranchEdit::class.java)
                                    && !branch.isDisabled
                                    && branch.type != BranchType.TEMPLATE_DEFINITION)
                        }
                ),
                // Template definition creation
                link(
                        "_templateDefinition",
                        { branch: Branch -> on(BranchController::class.java).getTemplateDefinition(branch.id) },
                        { branch, resourceContext ->
                            (branch.type != BranchType.TEMPLATE_INSTANCE
                                    && structureService.getBuildCount(branch) == 0
                                    && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java))
                        }
                ),
                // Template synchronisation
                link(
                        "_templateSync",
                        { branch: Branch -> on(BranchController::class.java).syncTemplateDefinition(branch.id) },
                        { branch, resourceContext -> branch.type == BranchType.TEMPLATE_DEFINITION && (resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java) || resourceContext.isProjectFunctionGranted(branch, BranchTemplateSync::class.java)) }
                ),
                // Template instance creation
                link(
                        "_templateInstanceCreate",
                        { branch: Branch -> on(BranchController::class.java).singleTemplateInstanceForm(branch.id) },
                        { branch, resourceContext -> branch.type == BranchType.TEMPLATE_DEFINITION && (resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java) || resourceContext.isProjectFunctionGranted(branch, BranchTemplateSync::class.java)) }
                ),
                // Template instance
                link(
                        "_templateInstance",
                        { branch: Branch -> on(BranchController::class.java).getTemplateInstance(branch.id) },
                        { branch, resourceContext -> branch.type == BranchType.TEMPLATE_INSTANCE && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java) }
                ),
                // Template instance disconnection
                link(
                        "_templateInstanceDisconnect",
                        { branch: Branch -> on(BranchController::class.java).disconnectTemplateInstance(branch.id) },
                        { branch, resourceContext -> branch.type == BranchType.TEMPLATE_INSTANCE && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java) }
                ),
                // Template instance connection
                link(
                        "_templateInstanceConnect",
                        { branch: Branch -> on(BranchController::class.java).connectTemplateInstance(branch.id) },
                        { branch, resourceContext -> branch.type == BranchType.CLASSIC && resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java) }
                ),
                // Template instance synchronisation
                link(
                        "_templateInstanceSync",
                        { branch: Branch -> on(BranchController::class.java).syncTemplateInstance(branch.id) },
                        { branch, resourceContext -> branch.type == BranchType.TEMPLATE_INSTANCE && (resourceContext.isProjectFunctionGranted(branch, BranchTemplateMgt::class.java) || resourceContext.isProjectFunctionGranted(branch, BranchTemplateSync::class.java)) }
                ),
                // Favourite --> 'unfavourite'
                link(
                        "_unfavourite",
                        { branch -> on(BranchController::class.java).unfavouriteBranch(branch.id) },
                        { branch, resourceContext -> resourceContext.isLogged && branchFavouriteService.isBranchFavourite(branch) }
                ),
                // Not favourite --> 'favourite'
                link(
                        "_favourite",
                        { branch -> on(BranchController::class.java).favouriteBranch(branch.id) },
                        { branch, resourceContext -> resourceContext.isLogged && !branchFavouriteService.isBranchFavourite(branch) }
                ),
                // Page
                page()
        ) + resourceDecorationContributorService.getLinkDefinitions(ProjectEntityType.BRANCH)
    }

}
