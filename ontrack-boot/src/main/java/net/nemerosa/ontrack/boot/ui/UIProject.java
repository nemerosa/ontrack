package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.boot.resource.Resource.link;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/ui/projects")
public class UIProject {

    private final ResourceAssembler resourceAssembler;

    @Autowired
    public UIProject(ResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
    }

    /**
     * FIXME List of projects
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<List<Resource<Project>>> projects() {
        List<Project> projects = Collections.emptyList();
        return Resource.of(
                projects
                        .stream()
                        .map(p -> resourceAssembler.toProjectResource(p, Collections.emptySet()))
                        .collect(Collectors.toList())
        )
                .self(link(fromMethodCall(on(UIProject.class).projects())))
                ;
    }

    /**
     * Gets a project.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Project> project(@PathVariable String id, @RequestParam(required = false) Set<String> follow) {
        // FIXME Calls the repository
        Project project = new Project(id, id, id);
        // Assembly
        Resource<Project> projectResource = resourceAssembler.toProjectResource(project, follow);
        // Follows the links
        projectResource = projectResource.follow(follow);
        // OK
        return projectResource;
    }

}
