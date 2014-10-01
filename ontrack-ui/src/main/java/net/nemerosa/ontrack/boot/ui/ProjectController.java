package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Replacements;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure/projects")
public class ProjectController extends AbstractResourceController {

    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public ProjectController(StructureService structureService, SecurityService securityService) {
        this.structureService = structureService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resources<Project> getProjectList() {
        return Resources.of(
                structureService.getProjectList(),
                uri(on(ProjectController.class).getProjectList())
        )
                .with(Link.CREATE, uri(on(ProjectController.class).newProject(null)), securityService.isGlobalFunctionGranted(ProjectCreation.class));
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public Form newProjectForm() {
        return Project.form();
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Project newProject(@RequestBody @Valid NameDescription nameDescription) {
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
        ).forView(BranchStatusView.class);
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
    public Form saveProjectForm(@PathVariable ID projectId) {
        return structureService.getProject(projectId).asForm();
    }

    @RequestMapping(value = "{projectId}/update", method = RequestMethod.PUT)
    public Project saveProject(@PathVariable ID projectId, @RequestBody @Valid NameDescription nameDescription) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Updates
        project = project.update(nameDescription);
        // Saves in repository
        structureService.saveProject(project);
        // As resource
        return project;
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

}
