package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Branch;
import net.nemerosa.ontrack.model.Project;
import net.nemerosa.ontrack.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/ui/projects")
public class UIProjectController implements UIProject {

    private final ResourceAssembler resourceAssembler;
    private final ProjectService projectService;

    @Autowired
    public UIProjectController(ResourceAssembler resourceAssembler, ProjectService projectService) {
        this.resourceAssembler = resourceAssembler;
        this.projectService = projectService;
    }

    /**
     * FIXME List of projects
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<List<Resource<Project>>> projects() {
        List<Project> projects = Collections.emptyList();
        return resourceAssembler.toProjectCollectionResource(projects);
    }

    /**
     * Gets a project.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Project> project(@PathVariable String id, @RequestParam(required = false) Set<String> follow) {
        // Calls the service
        Project project = projectService.getProject(id);
        // Assembly
        Resource<Project> projectResource = resourceAssembler.toProjectResource(project, follow);
        // Follows the links
        projectResource = projectResource.follow(follow);
        // OK
        return projectResource;
    }

    /**
     * FIXME List of branches for a project
     */
    @RequestMapping(value = "/projects/{project}/branches", method = RequestMethod.GET)
    public Resource<List<Resource<Branch>>> getBranchesForProject(@PathVariable String project) {
        List<Branch> branches = Collections.emptyList();
        return resourceAssembler.toBranchCollectionResource(project, branches);
    }

    /**
     * FIXME Gets a branch.
     */
    @RequestMapping(value = "/branches/{id}", method = RequestMethod.GET)
    public Resource<Branch> getBranch(@PathVariable String id) {
        Branch branch = new Branch(id, id, id);
        return resourceAssembler.toBranchResource(branch);
    }

}
