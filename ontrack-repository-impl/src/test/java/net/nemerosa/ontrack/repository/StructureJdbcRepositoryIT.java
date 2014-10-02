package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StructureJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private StructureRepository repository;

    @Test(expected = IllegalArgumentException.class)
    public void create_branch_project_not_defined() {
        repository.newBranch(Branch.of(
                Project.of(nameDescription()),
                nameDescription()
        ));
    }

    @Test
    public void create_branch() {
        // Creates a project
        Project project = do_create_project();
        // Creates a branch for this project
        Branch branch = repository.newBranch(Branch.of(project, nameDescription()));
        // Checks
        assertNotNull("Branch is defined", branch);
        assertTrue("Branch ID is defined", ID.isDefined(branch.getId()));
    }

    @Test
    public void create_project() {
        Project p = do_create_project();
        assertNotNull("Project is defined", p);
        assertTrue("Project ID is defined", ID.isDefined(p.getId()));
    }

    @Test
    public void save_project_disabled() {
        Project p = do_create_project();
        p = p.withDisabled(true);
        repository.saveProject(p);
        p = repository.getProject(p.getId());
        assertTrue("Project must be disabled", p.isDisabled());
    }

    private Project do_create_project() {
        return repository.newProject(Project.of(nameDescription()));
    }

}
