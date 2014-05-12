package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class StructureAPIController extends AbstractResourceController implements StructureAPI {

    private final StructureFactory structureFactory;
    private final StructureRepository structureRepository;

    @Autowired
    public StructureAPIController(StructureFactory structureFactory, StructureRepository structureRepository) {
        this.structureFactory = structureFactory;
        this.structureRepository = structureRepository;
    }

    @Override
    @RequestMapping(value = "projects", method = RequestMethod.GET)
    public List<Project> getProjectList() {
        return structureRepository.getProjectList();
        // TODO Create link
    }

    @Override
    @RequestMapping(value = "projects/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Project> newProject(@RequestBody NameDescription nameDescription) {
        // Creates a new project instance
        Project project = structureFactory.newProject(nameDescription);
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
        return toProjectResource(project);
    }

    private Resource<Project> toProjectResource(Project project) {
        return Resource.of(
                project,
                uri(on(StructureAPIController.class).getProject(project.getId()))
        );
        // TODO Update link
        // TODO Delete link
        // TODO View link
    }
}
