package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public ResourceCollection<Project> getProjectList() {
        return ResourceCollection.of(
                structureService.getProjectList().stream().map(this::toProjectResource),
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
    public Resource<Project> newProject(@RequestBody NameDescription nameDescription) {
        // Creates a new project instance
        Project project = Project.of(nameDescription);
        // Saves it into the repository
        project = structureService.newProject(project);
        // OK
        return toProjectResource(project);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
    public Resource<Project> getProject(@PathVariable ID projectId) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // As resource
        return toProjectResourceWithActions(project);
    }

    @RequestMapping(value = "{projectId}/update", method = RequestMethod.GET)
    public Form saveProjectForm(@PathVariable ID projectId) {
        return structureService.getProject(projectId).asForm();
    }

    @RequestMapping(value = "{projectId}/update", method = RequestMethod.PUT)
    public Resource<Project> saveProject(@PathVariable ID projectId, @RequestBody NameDescription nameDescription) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Updates
        project = project.update(nameDescription);
        // Saves in repository
        structureService.saveProject(project);
        // As resource
        return toProjectResource(project);
    }

    // Resource assemblers

    private Resource<Project> toProjectResourceWithActions(Project project) {
        return toProjectResource(project)
                // Update
                .with(
                        Link.UPDATE,
                        uri(on(ProjectController.class).saveProject(project.getId(), null)),
                        securityService.isProjectFunctionGranted(project.id(), ProjectEdit.class)
                )
                        // Branch list
                .with("branches", uri(on(BranchController.class).getBranchListForProject(project.getId())))
                ;
        // TODO Delete link
        // TODO View link
    }

    private Resource<Project> toProjectResource(Project project) {
        return Resource.of(
                project,
                uri(on(ProjectController.class).getProject(project.getId()))
        );
    }
}
