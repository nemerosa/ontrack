package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

public class StructureJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Test(expected = IllegalStateException.class)
    public void create_branch_project_not_defined() {
        structureRepository.newBranch(Branch.of(
                Project.of(nameDescription()),
                nameDescription()
        ));
    }

    @Test
    public void create_branch() {
        // Creates a project
        Project project = do_create_project();
        // Creates a branch for this project
        Branch branch = structureRepository.newBranch(Branch.of(project, nameDescription()));
        // Checks
        assertNotNull("Branch is defined", branch);
        assertTrue("Branch ID is defined", ID.isDefined(branch.getId()));
    }

    @Test
    public void create_build_with_long_name() {
        // Creates a ranch
        Branch branch = do_create_branch();
        // Creates a build for this branch, with a very long name
        String buildName = StringUtils.repeat("b", 150);
        Build build = structureRepository.newBuild(Build.of(
                branch,
                NameDescription.nd(buildName, ""),
                Signature.of("test")
        ));
        // Checks
        assertNotNull("Build is defined", build);
        assertTrue("Build ID is defined", ID.isDefined(build.getId()));
    }

    @Test
    public void create_branch_with_null_description() {
        // Creates a project
        Project project = do_create_project();
        // Creates a branch for this project
        Branch branch = structureRepository.newBranch(Branch.of(project, NameDescription.nd("B", null)));
        // Checks
        assertNotNull("Branch is defined", branch);
        assertNull(branch.getDescription());
        assertTrue("Branch ID is defined", ID.isDefined(branch.getId()));
    }

    @Test
    public void save_branch_disabled() {
        // Creates a project
        Project project = do_create_project();
        // Creates a branch for this project
        Branch branch = structureRepository.newBranch(Branch.of(project, nameDescription()));
        // Disables it
        branch = branch.withDisabled(true);
        // Saves it
        structureRepository.saveBranch(branch);
        // Retrieves it
        branch = structureRepository.getBranch(branch.getId());
        // Checks it is disabled
        assertTrue("Branch must be disabled", branch.isDisabled());
    }

    @Test
    public void create_project() {
        Project p = do_create_project();
        assertNotNull("Project is defined", p);
        assertTrue("Project ID is defined", ID.isDefined(p.getId()));
    }

    @Test
    public void create_project_with_null_description() {
        Project p = structureRepository.newProject(Project.of(NameDescription.nd(uid("P"), null)));
        assertNotNull("Project is defined", p);
        assertNull(p.getDescription());
        assertTrue("Project ID is defined", ID.isDefined(p.getId()));
    }

    @Test
    public void save_project_disabled() {
        Project p = do_create_project();
        p = p.withDisabled(true);
        structureRepository.saveProject(p);
        p = structureRepository.getProject(p.getId());
        assertTrue("Project must be disabled", p.isDisabled());
    }

}
