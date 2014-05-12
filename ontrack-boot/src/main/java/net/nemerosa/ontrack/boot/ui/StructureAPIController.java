package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureFactory;
import net.nemerosa.ontrack.model.structure.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/structure")
@Component
public class StructureAPIController implements StructureAPI {

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
    }

    @Override
    @POST
    public Project newProject(NameDescription nameDescription) {
        // Creates a new project instance
        Project project = structureFactory.newProject(nameDescription);
        // Saves it into the repository
        return structureRepository.newProject(project);
    }
}
