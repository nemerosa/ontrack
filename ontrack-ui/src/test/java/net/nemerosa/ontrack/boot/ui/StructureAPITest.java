package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.BranchCreate;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.Assert.*;

public class StructureAPITest extends AbstractWebTestSupport {

    @Autowired
    private StructureAPI structure;

    @Test
    public void createProject() throws Exception {
        asUser().with(ProjectCreation.class).call(() -> {
            NameDescription nameDescription = nameDescription();
            Resource<Project> resource = structure.newProject(nameDescription);
            checkProjectResource(resource, nameDescription);
            return null;
        });
    }

    @Test(expected = AccessDeniedException.class)
    public void createProject_denied() throws Exception {
        asUser().call(() -> structure.newProject(nameDescription()));
    }

    @Test
    public void updateProject() throws Exception {
        // Creates the project
        NameDescription initialNames = nameDescription();
        Resource<Project> resource = asUser().with(ProjectCreation.class).call(() -> structure.newProject(initialNames));
        ID id = resource.getData().getId();
        // Edition
        asUser().with(id.getValue(), ProjectEdit.class).call(() -> {
            // Updates
            NameDescription nameDescription = nameDescription();
            assertNotEquals(initialNames, nameDescription);
            Resource<Project> updated = structure.saveProject(id, nameDescription);
            // Checks
            checkProjectResource(updated, nameDescription);
            // Gets the project back
            updated = structure.getProject(id);
            checkProjectResource(updated, nameDescription);
            return null;
        });
    }

    @Test
    public void createBranch() throws Exception {
        // Project
        Resource<Project> project = asUser().with(ProjectCreation.class).call(() -> structure.newProject(nameDescription()));
        // Branch
        NameDescription nameDescription = nameDescription();
        Resource<Branch> resource = asUser()
                .with(project.getData().id(), BranchCreate.class)
                .call(() -> structure.newBranch(project.getData().getId(), nameDescription));
        // Checks the branch
        checkBranchResource(resource, nameDescription);
    }

    @Test(expected = AccessDeniedException.class)
    public void createBranch_denied() throws Exception {
        // Project
        Resource<Project> project = asUser().with(ProjectCreation.class).call(() -> structure.newProject(nameDescription()));
        // Branch
        asUser().call(() -> structure.newBranch(project.getData().getId(), nameDescription()));
    }

    private void checkProjectResource(Resource<Project> resource, NameDescription nameDescription) {
        assertNotNull("Resource not null", resource);
        Project project = resource.getData();
        assertNotNull("Project not null", project);
        assertNotNull("Project ID not null", project.getId());
        assertTrue("Project ID set", project.getId().isSet());
        assertEquals("Project name", nameDescription.getName(), project.getName());
        assertEquals("Project description", nameDescription.getDescription(), project.getDescription());
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
