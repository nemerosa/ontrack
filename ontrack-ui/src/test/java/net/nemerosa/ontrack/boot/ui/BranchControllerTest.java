package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.BranchCreate;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.Assert.*;

public class BranchControllerTest extends AbstractWebTestSupport {

    @Autowired
    private ProjectController projectController;
    @Autowired
    private BranchController controller;

    @Test
    public void createBranch() throws Exception {
        // Project
        Resource<Project> project = asUser().with(ProjectCreation.class).call(() -> projectController.newProject(nameDescription()));
        // Branch
        NameDescription nameDescription = nameDescription();
        Resource<Branch> resource = asUser()
                .with(project.getData().id(), BranchCreate.class)
                .call(() -> controller.newBranch(project.getData().getId(), nameDescription));
        // Checks the branch
        checkBranchResource(resource, nameDescription);
    }

    @Test(expected = AccessDeniedException.class)
    public void createBranch_denied() throws Exception {
        // Project
        Resource<Project> project = asUser().with(ProjectCreation.class).call(() -> projectController.newProject(nameDescription()));
        // Branch
        asUser().call(() -> controller.newBranch(project.getData().getId(), nameDescription()));
    }

    private void checkBranchResource(Resource<Branch> resource, NameDescription nameDescription) {
        assertNotNull("Resource not null", resource);
        Branch branch = resource.getData();
        assertNotNull("Branch not null", branch);
        assertNotNull("Branch ID not null", branch.getId());
        assertTrue("Branch ID set", branch.getId().isSet());
        assertEquals("Branch name", nameDescription.getName(), branch.getName());
        assertEquals("Branch description", nameDescription.getDescription(), branch.getDescription());
    }

}
