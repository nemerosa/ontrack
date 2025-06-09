package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.*
import net.nemerosa.ontrack.model.labels.ProjectLabelManagement
import net.nemerosa.ontrack.model.security.ProjectAuthorisationMgt
import net.nemerosa.ontrack.model.security.ProjectDelete
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService
import net.nemerosa.ontrack.ui.resource.*
import net.nemerosa.ontrack.ui.resource.LinkDefinitions.link
import net.nemerosa.ontrack.ui.resource.LinkDefinitions.page
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ProjectResourceDecorator(
        private val resourceDecorationContributorService: ResourceDecorationContributorService,
        private val projectFavouriteService: ProjectFavouriteService
) : AbstractLinkResourceDecorator<Project>(Project::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<Project>> {
        return listOf(
                link(Link.SELF) { project -> on(ProjectController::class.java).getProject(project.id) },
                // List of branches for this project
                link("_branches") { project -> on(BranchController::class.java).getBranchListForProject(project.id) },
                // List of branches and their views
                link("_branchStatusViews") { project -> on(ProjectController::class.java).getBranchStatusViews(project.id) },
                // Build diff actions
                link("_buildDiffActions") { project -> on(BuildController::class.java).buildDiffActions(project.id) },
                // Actual properties for this project
                link("_properties") { project -> on(PropertyController::class.java).getProperties(ProjectEntityType.PROJECT, project.id) },
                // Extra information
                link("_extra") { project ->
                    on(ProjectEntityExtensionController::class.java).getInformation(ProjectEntityType.PROJECT, project.id)
                },
                // Actions
                link("_actions") { project -> on(ProjectEntityExtensionController::class.java).getActions(ProjectEntityType.PROJECT, project.id) },
                // Updating the project
                Link.UPDATE linkTo { project: Project ->
                    on(ProjectController::class.java).saveProject(project.id, null)
                } linkIf (ProjectEdit::class),
                // Delete link
                Link.DELETE linkTo { project: Project ->
                    on(ProjectController::class.java).deleteProject(project.id)
                } linkIf (ProjectDelete::class),
                // Decorations
                link("_decorations") { project -> on(DecorationsController::class.java).getDecorations(project.projectEntityType, project.id) },
                // Authorisation management
                "_permissions" linkTo { project: Project ->
                    on(PermissionController::class.java).getProjectPermissions(project.id)
                } linkIf (ProjectAuthorisationMgt::class),
                // Events
                link("_events") { project -> on(EventController::class.java).getEvents(project.projectEntityType, project.id, 0, 10) },
                // Enable
                link(
                        "_enable",
                        { project -> on(ProjectController::class.java).enableProject(project.id) },
                        { project, resourceContext -> resourceContext.isProjectFunctionGranted(project.id(), ProjectEdit::class.java) && project.isDisabled }
                ),
                // Disable
                link(
                        "_disable",
                        { project -> on(ProjectController::class.java).disableProject(project.id) },
                        { project, resourceContext -> resourceContext.isProjectFunctionGranted(project.id(), ProjectEdit::class.java) && !project.isDisabled }
                ),
                // Favourite --> 'unfavourite'
                link(
                        "_unfavourite",
                        { project -> on(ProjectController::class.java).unfavouriteProject(project.id) },
                        { project, resourceContext -> resourceContext.isLogged && projectFavouriteService.isProjectFavourite(project) }
                ),
                // Not favourite --> 'favourite'
                link(
                        "_favourite",
                        { project -> on(ProjectController::class.java).favouriteProject(project.id) },
                        { project, resourceContext -> resourceContext.isLogged && !projectFavouriteService.isProjectFavourite(project) }
                ),
                // Setting the project labels
                link(
                        "_labels",
                        { project -> on(ProjectLabelController::class.java).getLabelsForProject(project.id()) },
                        { project, resourceContext -> resourceContext.isProjectFunctionGranted(project, ProjectLabelManagement::class.java) }
                ),
                // Page
                page()
        ) + resourceDecorationContributorService.getLinkDefinitions(ProjectEntityType.PROJECT)
    }

}
