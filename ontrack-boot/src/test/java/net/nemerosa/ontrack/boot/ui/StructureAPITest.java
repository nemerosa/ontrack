package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class StructureAPITest extends AbstractWebTestSupport {

    @Autowired
    private StructureAPI structure;

    @Test
    public void createProject() {
        NameDescription nameDescription = nameDescription();
        Resource<Project> resource = structure.newProject(nameDescription);
        checkProjectResource(resource, nameDescription);
    }

    @Test
    public void updateProject() {
        // Creates the project
        NameDescription initialNames = nameDescription();
        Resource<Project> resource = structure.newProject(initialNames);
        ID id = resource.getData().getId();
        // Updates
        NameDescription nameDescription = nameDescription();
        assertNotEquals(initialNames, nameDescription);
        resource = structure.saveProject(id, nameDescription);
        // Checks
        checkProjectResource(resource, nameDescription);
        // Gets the project back
        resource = structure.getProject(id);
        checkProjectResource(resource, nameDescription);
    }

    @Test
    public void createBranch() {
        // Project
        Resource<Project> project = structure.newProject(nameDescription());
        // Branch
        NameDescription nameDescription = nameDescription();
        Resource<Branch> resource = structure.newBranch(project.getData().getId(), nameDescription);
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
