package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.boot.resource.Link.link;
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
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public Resource<List<Resource<Project>>> projects() {
        List<Project> projects = Collections.emptyList();
        return Resource.of(
                projects
                        .stream()
                        .map(resourceAssembler::toProjectResource)
                        .collect(Collectors.toList())
        )
                .self(link(fromMethodCall(on(UIProject.class).projects())))
                ;
    }

    /**
     * FIXME Gets a project.
     */
    @RequestMapping(value = "/projects/{id}", method = RequestMethod.GET)
    public Resource<Project> project(@PathVariable String id) {
        Project project = new Project(id, id, id);
        return resourceAssembler.toProjectResource(project);
    }

}
