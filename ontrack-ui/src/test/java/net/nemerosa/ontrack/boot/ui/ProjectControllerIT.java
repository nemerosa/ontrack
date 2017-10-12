package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescriptionState;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.Assert.*;

public class ProjectControllerIT extends AbstractWebTestSupport {

    @Autowired
    private ProjectController controller;

    @Test
    public void createProject() throws Exception {
        asUser().with(ProjectCreation.class).call(() -> {
            NameDescriptionState nameDescription = nameDescription().asState();
            Project resource = controller.newProject(nameDescription);
            checkProject(resource, nameDescription);
            return null;
        });
    }

    @Test(expected = AccessDeniedException.class)
    public void createProject_denied() throws Exception {
        asUser().call(() -> controller.newProject(nameDescription().asState()));
    }

    @Test
    public void disablingEnablingProject() throws Exception {
        Project project = doCreateProject();
        // Disables it
        Project disabled = asUser().with(project, ProjectEdit.class).call(
                () -> controller.disableProject(project.getId())
        );
        assertTrue("Project is disabled", disabled.isDisabled());
        // Enables it
        Project enabled = asUser().with(project, ProjectEdit.class).call(
                () -> controller.enableProject(project.getId())
        );
        assertFalse("Project is enabled", enabled.isDisabled());
    }

    @Test
    public void updateProject() throws Exception {
        // Creates the project
        NameDescriptionState initialNames = nameDescription().asState();
        Project project = asUser().with(ProjectCreation.class).call(() -> controller.newProject(initialNames));
        ID id = project.getId();
        // Edition
        asUser().with(id.getValue(), ProjectEdit.class).call(() -> {
            // Updates
            NameDescriptionState nameDescription = nameDescription().asState();
            assertNotEquals(initialNames, nameDescription);
            Project updated = controller.saveProject(id, nameDescription);
            // Checks
            checkProject(updated, nameDescription);
            // Gets the project back
            updated = controller.getProject(id);
            checkProject(updated, nameDescription);
            return null;
        });
    }

    private void checkProject(Project project, NameDescriptionState nameDescription) {
        assertNotNull("Project not null", project);
        assertNotNull("Project ID not null", project.getId());
        assertTrue("Project ID set", project.getId().isSet());
        assertEquals("Project name", nameDescription.getName(), project.getName());
        assertEquals("Project description", nameDescription.getDescription(), project.getDescription());
        assertEquals("Project state", nameDescription.isDisabled(), project.isDisabled());
    }

}
