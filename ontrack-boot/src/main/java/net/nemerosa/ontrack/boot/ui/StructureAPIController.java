package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class StructureAPIController extends AbstractResourceController implements StructureAPI {

    private final StructureRepository structureRepository;

    @Autowired
    public StructureAPIController(StructureRepository structureRepository) {
        this.structureRepository = structureRepository;
    }

    @Override
    @RequestMapping(value = "projects", method = RequestMethod.GET)
    public ResourceCollection<Project> getProjectList() {
        return ResourceCollection.of(
                structureRepository.getProjectList().stream().map(this::toProjectResource),
                uri(on(StructureAPIController.class).getProjectList())
        )
                // TODO Authorization
                .with(Link.CREATE, uri(on(StructureAPIController.class).newProject(null)));
    }

    @Override
    @RequestMapping(value = "projects/create", method = RequestMethod.GET)
    public Form newProjectForm() {
        return Project.form();
    }

    @Override
    @RequestMapping(value = "projects/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Project> newProject(@RequestBody NameDescription nameDescription) {
        // Creates a new project instance
        Project project = Project.of(nameDescription);
        // Saves it into the repository
        project = structureRepository.newProject(project);
        // OK
        return toProjectResource(project);
    }

    @Override
    @RequestMapping(value = "projects/{projectId}", method = RequestMethod.GET)
    public Resource<Project> getProject(@PathVariable ID projectId) {
        // Gets from the repository
        Project project = structureRepository.getProject(projectId);
        // As resource
        return toProjectResourceWithActions(project);
    }

    @Override
    @RequestMapping(value = "projects/{projectId}/update", method = RequestMethod.GET)
    public Form saveProjectForm(@PathVariable ID projectId) {
        return structureRepository.getProject(projectId).asForm();
    }

    @Override
    @RequestMapping(value = "projects/{projectId}/update", method = RequestMethod.PUT)
    public Resource<Project> saveProject(@PathVariable ID projectId, @RequestBody NameDescription nameDescription) {
        // Gets from the repository
        Project project = structureRepository.getProject(projectId);
        // Updates
        project = project.update(nameDescription);
        // Saves in repository
        structureRepository.saveProject(project);
        // As resource
        return toProjectResource(project);
    }

    @Override
    @RequestMapping(value = "projects/{projectId}/branches", method = RequestMethod.GET)
    public ResourceCollection<Branch> getBranchListForProject(@PathVariable ID projectId) {
        return ResourceCollection.of(
                structureRepository.getBranchesForProject(projectId).stream().map(this::toBranchResource),
                uri(on(StructureAPIController.class).getBranchListForProject(projectId))
        )
                // TODO Create (authorization)
                .with(Link.CREATE, uri(on(StructureAPIController.class).newBranch(projectId, null)))
                ;
    }

    @Override
    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.GET)
    public Form newBranchForm() {
        return Branch.form();
    }

    @Override
    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.POST)
    public Resource<Branch> newBranch(@PathVariable ID projectId, @RequestBody NameDescription nameDescription) {
        // Gets the project
        Project project = structureRepository.getProject(projectId);
        // Creates a new branch instance
        Branch branch = Branch.of(project, nameDescription);
        // Saves it into the repository
        branch = structureRepository.newBranch(branch);
        // OK
        return toBranchResource(branch);
    }

    @Override
    @RequestMapping(value = "branches/{branchId}", method = RequestMethod.GET)
    public Resource<Branch> getBranch(@PathVariable ID branchId) {
        return toBranchResourceWithActions(
                structureRepository.getBranch(branchId)
        );
    }

    // Builds

    @Override
    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.GET)
    public Form newBuildForm() {
        return Build.form();
    }

    @Override
    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.POST)
    public Resource<Build> newBuild(@PathVariable ID branchId, @RequestBody NameDescription nameDescription) {
        // Gets the holding branch
        Branch branch = structureRepository.getBranch(branchId);
        // TODO Build signature
        Signature signature = Signature.of("TODO User");
        // Creates a new build
        Build build = Build.of(branch, nameDescription, signature);
        // Saves it into the repository
        build = structureRepository.newBuild(build);
        // OK
        return toBuildResource(build);
    }

    // Resource assemblers

    private Resource<Project> toProjectResourceWithActions(Project project) {
        return toProjectResource(project)
                // TODO Update link (authorization)
                .with(Link.UPDATE, uri(on(StructureAPIController.class).saveProject(project.getId(), null)))
                        // Branch list
                .with("branches", uri(on(StructureAPIController.class).getBranchListForProject(project.getId())))
                ;
        // TODO Delete link
        // TODO View link
    }

    private Resource<Project> toProjectResource(Project project) {
        return Resource.of(
                project,
                uri(on(StructureAPIController.class).getProject(project.getId()))
        );
    }

    private Resource<Branch> toBranchResourceWithActions(Branch branch) {
        return toBranchResource(branch)
                // TODO Update link (with authorisation)
                // TODO Delete link
                // TODO View link
                // TODO Builds link
                ;
    }

    private Resource<Branch> toBranchResource(Branch branch) {
        return Resource.of(
                branch,
                uri(on(StructureAPIController.class).getBranch(branch.getId()))
                // TODO Branch's project
        );
    }

    private Resource<Build> toBuildResource(Build build) {
        return Resource.of(
                build,
                // TODO Build link
                URI.create("urn:build")
        );
    }
}
