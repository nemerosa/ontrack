package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.it.AbstractITTestJUnit4Support;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRepositoryTestSupport extends AbstractITTestJUnit4Support {

    @Autowired
    protected StructureRepository structureRepository;

    protected Project do_create_project() {
        return structureRepository.newProject(Project.of(nameDescription()));
    }

    protected Branch do_create_branch() {
        return structureRepository.newBranch(Branch.of(do_create_project(), nameDescription()));
    }

}
