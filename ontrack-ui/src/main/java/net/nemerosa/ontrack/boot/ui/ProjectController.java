package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Replacements;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.BranchCreate;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import net.nemerosa.ontrack.ui.support.API;
import net.nemerosa.ontrack.ui.support.APIMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure/projects")
@API("Management of projects")
public class ProjectController extends AbstractResourceController {

    private final StructureService structureService;
    private final CopyService copyService;
    private final SecurityService securityService;
    private final ProjectFavouriteService projectFavouriteService;

    @Autowired
    public ProjectController(StructureService structureService, CopyService copyService, SecurityService securityService, ProjectFavouriteService projectFavouriteService) {
        this.structureService = structureService;
        this.copyService = copyService;
        this.securityService = securityService;
        this.projectFavouriteService = projectFavouriteService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resources<Project> getProjectList() {
        return Resources.of(
                structureService.getProjectList(),
                uri(on(ProjectController.class).getProjectList())
        )
                .with(Link.CREATE, uri(on(ProjectController.class).newProject(null)), securityService.isGlobalFunctionGranted(ProjectCreation.class));
    }

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public Resources<ProjectStatusView> getProjectStatusViews() {
        return Resources.of(
                structureService.getProjectStatusViews(),
                uri(on(ProjectController.class).getProjectStatusViews())
        )
                .forView(ProjectStatusView.class)
                .with(Link.CREATE, uri(on(ProjectController.class).newProject(null)), securityService.isGlobalFunctionGranted(ProjectCreation.class));
    }

    @RequestMapping(value = "favourites", method = RequestMethod.GET)
    public Resources<ProjectStatusView> getProjectStatusViewsForFavourites() {
        return Resources.of(
                structureService.getProjectStatusViewsForFavourites(),
                uri(on(ProjectController.class).getProjectStatusViewsForFavourites())
        )
                .forView(ProjectStatusView.class)
                .with(Link.CREATE, uri(on(ProjectController.class).newProject(null)), securityService.isGlobalFunctionGranted(ProjectCreation.class));
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public Form newProjectForm() {
        return Project.form();
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Project newProject(@RequestBody @Valid NameDescriptionState nameDescription) {
        // Creates a new project instance
        Project project = Project.of(nameDescription);
        // Saves it into the repository
        project = structureService.newProject(project);
        // OK
        return project;
    }

    @RequestMapping(value = "{projectId}/view", method = RequestMethod.GET)
    public Resources<BranchStatusView> getBranchStatusViews(@PathVariable ID projectId) {
        return Resources.of(
                structureService.getBranchStatusViews(projectId),
                uri(on(ProjectController.class).getBranchStatusViews(projectId))
        )
                .forView(BranchStatusView.class)
                .with(
                        Link.CREATE,
                        uri(on(BranchController.class).newBranch(projectId, null)),
                        securityService.isProjectFunctionGranted(projectId.get(), BranchCreate.class)
                );
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
    public Project getProject(@PathVariable ID projectId) {
        return structureService.getProject(projectId);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.DELETE)
    public Ack deleteProject(@PathVariable ID projectId) {
        return structureService.deleteProject(projectId);
    }

    @RequestMapping(value = "{projectId}/update", method = RequestMethod.GET)
    @APIMethod(value = "Project update form", description = "Get the form for the project update")
    public Form saveProjectForm(@PathVariable ID projectId) {
        return structureService.getProject(projectId).asForm();
    }

    @RequestMapping(value = "{projectId}/update", method = RequestMethod.PUT)
    @APIMethod(value = "Updates project")
    public Project saveProject(@PathVariable ID projectId, @RequestBody @Valid NameDescriptionState nameDescription) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Updates
        project = project.update(nameDescription);
        // Saves in repository
        structureService.saveProject(project);
        // As resource
        return project;
    }

    @RequestMapping(value = "{projectId}/enable", method = RequestMethod.PUT)
    public Project enableProject(@PathVariable ID projectId) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Saves in repository
        return structureService.enableProject(project);
    }

    @RequestMapping(value = "{projectId}/disable", method = RequestMethod.PUT)
    public Project disableProject(@PathVariable ID projectId) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Saves in repository
        return structureService.disableProject(project);
    }

    /**
     * Gets the form to clone this project into another projevt
     */
    @RequestMapping(value = "{projectId}/clone", method = RequestMethod.GET)
    public Form clone(@SuppressWarnings("UnusedParameters") @PathVariable ID projectId) {
        return Form.create()
                .with(
                        Text.of("name")
                                .label("Target project")
                                .help("Name of the project to create")
                )
                .with(
                        Replacements.of("replacements")
                                .label("Replacements")
                )
                ;
    }

    /**
     * Clones this project into another one.
     */
    @RequestMapping(value = "{projectId}/clone", method = RequestMethod.POST)
    public Project clone(@PathVariable ID projectId, @RequestBody ProjectCloneRequest request) {
        // Gets the project
        Project project = structureService.getProject(projectId);
        // Performs the clone
        return copyService.cloneProject(project, request);
    }

    @RequestMapping(value = "{projectId}/favourite", method = RequestMethod.PUT)
    public Project favouriteProject(@PathVariable ID projectId) {
        Project project = structureService.getProject(projectId);
        projectFavouriteService.setProjectFavourite(project, true);
        return project;
    }

    @RequestMapping(value = "{projectId}/unfavourite", method = RequestMethod.PUT)
    public Project unfavouriteProject(@PathVariable ID projectId) {
        Project project = structureService.getProject(projectId);
        projectFavouriteService.setProjectFavourite(project, false);
        return project;
    }

}
