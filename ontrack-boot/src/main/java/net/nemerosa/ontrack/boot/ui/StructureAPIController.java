package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/structure")
@Component
public class StructureAPIController extends AbstractResourceController implements StructureAPI {

    private final StructureFactory structureFactory;
    private final StructureRepository structureRepository;

    @Autowired
    public StructureAPIController(StructureFactory structureFactory, StructureRepository structureRepository) {
        this.structureFactory = structureFactory;
        this.structureRepository = structureRepository;
    }

    @Override
    @GET
    public List<Project> getProjectList() {
        return structureRepository.getProjectList();
        // TODO Create link
    }

    @Override
    @Path("create")
    @POST
    public Response newProject(NameDescription nameDescription) {
        // Creates a new project instance
        Project project = structureFactory.newProject(nameDescription);
        // Saves it into the repository
        project = structureRepository.newProject(project);
        // OK
        return Response.created(uriBuilder("getProject").build(project.getId());
    }

    @Override
    @Path("{projectId}")
    @GET
    public Resource<Project> getProject(@PathParam("projectId") ID projectId) {
        // Gets from the repository
        Project project = structureRepository.getProject(projectId);
        // As resource
        return Resource.of(
                project,
                uriBuilder("getProject").build(projectId)
        )
                // TODO Update link
                // TODO Delete link
                // TODO View link
                ;
    }
}
