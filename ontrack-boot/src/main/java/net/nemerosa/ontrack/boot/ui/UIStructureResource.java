package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureFactory;
import net.nemerosa.ontrack.model.structure.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;

@Path("/structure")
@Component
public class UIStructureResource implements UIStructure {

    private final StructureFactory structureFactory;
    private final StructureRepository structureRepository;

    @Autowired
    public UIStructureResource(StructureFactory structureFactory, StructureRepository structureRepository) {
        this.structureFactory = structureFactory;
        this.structureRepository = structureRepository;
    }

    @Override
    public Project newProject(NameDescription nameDescription) {
        // Creates a new project instance
        Project project = structureFactory.newProject(nameDescription);
        // Saves it into the repository
        return structureRepository.newProject(project);
    }
}
