package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.Assert.*;

public class ProjectControllerTest extends AbstractWebTestSupport {

    @Autowired
    private ProjectController controller;

    @Test
    public void createProject() throws Exception {
        asUser().with(ProjectCreation.class).call(() -> {
            NameDescription nameDescription = nameDescription();
            Resource<Project> resource = controller.newProject(nameDescription);
            checkProjectResource(resource, nameDescription);
            return null;
        });
    }

    @Test(expected = AccessDeniedException.class)
    public void createProject_denied() throws Exception {
        asUser().call(() -> controller.newProject(nameDescription()));
    }

    @Test
    public void updateProject() throws Exception {
        // Creates the project
        NameDescription initialNames = nameDescription();
        Resource<Project> resource = asUser().with(ProjectCreation.class).call(() -> controller.newProject(initialNames));
        ID id = resource.getData().getId();
        // Edition
        asUser().with(id.getValue(), ProjectEdit.class).call(() -> {
            // Updates
            NameDescription nameDescription = nameDescription();
            assertNotEquals(initialNames, nameDescription);
            Resource<Project> updated = controller.saveProject(id, nameDescription);
            // Checks
            checkProjectResource(updated, nameDescription);
            // Gets the project back
            updated = controller.getProject(id);
            checkProjectResource(updated, nameDescription);
            return null;
        });
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

}
