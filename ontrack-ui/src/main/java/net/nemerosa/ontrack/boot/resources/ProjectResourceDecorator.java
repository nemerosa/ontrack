package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService;
import net.nemerosa.ontrack.ui.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ProjectResourceDecorator extends AbstractResourceDecorator<Project> {

    private final ResourceDecorationContributorService resourceDecorationContributorService;
    private final ProjectFavouriteService projectFavouriteService;

    @Autowired
    public ProjectResourceDecorator(ResourceDecorationContributorService resourceDecorationContributorService, ProjectFavouriteService projectFavouriteService) {
        super(Project.class);
        this.resourceDecorationContributorService = resourceDecorationContributorService;
        this.projectFavouriteService = projectFavouriteService;
    }

    @Override
    public List<Link> links(Project project, ResourceContext resourceContext) {
        boolean projectFavourite = projectFavouriteService.isProjectFavourite(project);
        LinksBuilder linksBuilder = resourceContext.links()
                .self(on(ProjectController.class).getProject(project.getId()))
                // List of branches for this project
                .link("_branches", on(BranchController.class).getBranchListForProject(project.getId()))
                // Creates a branch for this project
                .link(
                        "_createBranch",
                        on(BranchController.class).newBranchForm(project.getId()),
                        BranchCreate.class, project
                )
                // List of branches and their views
                .link("_branchStatusViews", on(ProjectController.class).getBranchStatusViews(project.getId()))
                // Build search
                .link("_buildSearch", on(BuildController.class).buildSearchForm(project.getId()))
                // Build diff actions
                .link("_buildDiffActions", on(BuildController.class).buildDiffActions(project.getId()))
                // Actual properties for this project
                .link("_properties", on(PropertyController.class).getProperties(ProjectEntityType.PROJECT, project.getId()))
                // Actions
                .link("_actions", on(ProjectEntityExtensionController.class).getActions(ProjectEntityType.PROJECT, project.getId()))
                // Updating the project
                .update(on(ProjectController.class).saveProject(project.getId(), null), ProjectEdit.class, project.id())
                // Delete link
                .delete(on(ProjectController.class).deleteProject(project.getId()), ProjectDelete.class, project.id())
                // Decorations
                .link("_decorations", on(DecorationsController.class).getDecorations(project.getProjectEntityType(), project.getId()))
                // Authorisation management
                .link("_permissions", on(PermissionController.class).getProjectPermissions(project.getId()), ProjectAuthorisationMgt.class, project.id())
                // Events
                .link("_events", on(EventController.class).getEvents(project.getProjectEntityType(), project.getId(), 0, 10))
                // Clone to another project
                .link(
                        "_clone",
                        on(ProjectController.class).clone(project.getId()),
                        ProjectCreation.class
                )
                // Enable
                .link(
                        "_enable",
                        on(ProjectController.class).enableProject(project.getId()),
                        resourceContext.isProjectFunctionGranted(project.id(), ProjectEdit.class)
                                && project.isDisabled()
                )
                // Disable
                .link(
                        "_disable",
                        on(ProjectController.class).disableProject(project.getId()),
                        resourceContext.isProjectFunctionGranted(project.id(), ProjectEdit.class)
                                && !project.isDisabled()
                )
                // Favourite --> 'unfavourite'
                .link(
                        "_unfavourite",
                        on(ProjectController.class).unfavouriteProject(project.getId()),
                        resourceContext.isLogged() && projectFavourite
                )
                // Not favourite --> 'favourite'
                .link(
                        "_favourite",
                        on(ProjectController.class).favouriteProject(project.getId()),
                        resourceContext.isLogged() && !projectFavourite
                )
                // Page
                .page(project);
        // Contributions
        resourceDecorationContributorService.contribute(linksBuilder, project);
        // OK
        return linksBuilder.build();
    }

    @Override
    public List<String> getLinkNames() {
        return Arrays.asList(
                // FIXME Use constants
                "_self",
                "_branches",
                "_createBranch",
                "_branchStatusViews",
                "_buildSearch",
                "_buildDiffActions",
                "_properties",
                "_actions",
                "_update",
                "_delete",
                "_decorations",
                "_permissions",
                "_events",
                "_clone",
                "_enable",
                "_disable",
                "_unfavourite",
                "_favourite",
                "_page"
        );
        // FIXME Link contributors
    }
}
