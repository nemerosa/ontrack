package net.nemerosa.ontrack.boot.resources;

import com.google.common.collect.Iterables;
import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService;
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
public class ProjectResourceDecorator extends AbstractLinkResourceDecorator<Project> {

    private final ResourceDecorationContributorService resourceDecorationContributorService;
    private final ProjectFavouriteService projectFavouriteService;

    @Autowired
    public ProjectResourceDecorator(ResourceDecorationContributorService resourceDecorationContributorService, ProjectFavouriteService projectFavouriteService) {
        super(Project.class);
        this.resourceDecorationContributorService = resourceDecorationContributorService;
        this.projectFavouriteService = projectFavouriteService;
    }

    protected Iterable<LinkDefinition<Project>> getLinkDefinitions() {
        return Iterables.concat(
                Arrays.asList(
                        link(Link.SELF, project -> on(ProjectController.class).getProject(project.getId())),
                        // List of branches for this project
                        link("_branches", project -> on(BranchController.class).getBranchListForProject(project.getId())),
                        // Creates a branch for this project
                        link(
                                "_createBranch",
                                project -> on(BranchController.class).newBranchForm(project.getId()),
                                withProjectFn(BranchCreate.class)
                        ),
                        // List of branches and their views
                        link("_branchStatusViews", project -> on(ProjectController.class).getBranchStatusViews(project.getId())),
                        // Build search
                        link("_buildSearch", project -> on(BuildController.class).buildSearchForm(project.getId())),
                        // Build diff actions
                        link("_buildDiffActions", project -> on(BuildController.class).buildDiffActions(project.getId())),
                        // Actual properties for this project
                        link("_properties", project -> on(PropertyController.class).getProperties(ProjectEntityType.PROJECT, project.getId())),
                        // Actions
                        link("_actions", project -> on(ProjectEntityExtensionController.class).getActions(ProjectEntityType.PROJECT, project.getId())),
                        // Updating the project
                        link(Link.UPDATE, project -> on(ProjectController.class).saveProject(project.getId(), null), withProjectFn(ProjectEdit.class)),
                        // Delete link
                        link(Link.DELETE, project -> on(ProjectController.class).deleteProject(project.getId()), withProjectFn(ProjectDelete.class)),
                        // Decorations
                        link("_decorations", project -> on(DecorationsController.class).getDecorations(project.getProjectEntityType(), project.getId())),
                        // Authorisation management
                        link("_permissions", project -> on(PermissionController.class).getProjectPermissions(project.getId()), withProjectFn(ProjectAuthorisationMgt.class)),
                        // Events
                        link("_events", project -> on(EventController.class).getEvents(project.getProjectEntityType(), project.getId(), 0, 10)),
                        // Clone to another project
                        link(
                                "_clone",
                                project -> on(ProjectController.class).clone(project.getId()),
                                withGlobalFn(ProjectCreation.class)
                        ),
                        // Enable
                        link(
                                "_enable",
                                project -> on(ProjectController.class).enableProject(project.getId()),
                                (project, resourceContext) -> resourceContext.isProjectFunctionGranted(project.id(), ProjectEdit.class)
                                        && project.isDisabled()
                        ),
                        // Disable
                        link(
                                "_disable",
                                project -> on(ProjectController.class).disableProject(project.getId()),
                                (project, resourceContext) -> resourceContext.isProjectFunctionGranted(project.id(), ProjectEdit.class)
                                        && !project.isDisabled()
                        ),
                        // Favourite --> 'unfavourite'
                        link(
                                "_unfavourite",
                                project -> on(ProjectController.class).unfavouriteProject(project.getId()),
                                (project, resourceContext) -> resourceContext.isLogged() && projectFavouriteService.isProjectFavourite(project)
                        ),
                        // Not favourite --> 'favourite'
                        link(
                                "_favourite",
                                project -> on(ProjectController.class).favouriteProject(project.getId()),
                                (project, resourceContext) -> resourceContext.isLogged() && !projectFavouriteService.isProjectFavourite(project)
                        ),
                        // Page
                        page()
                ),
                resourceDecorationContributorService.getLinkDefinitions(ProjectEntityType.PROJECT)
        );
    }

}
