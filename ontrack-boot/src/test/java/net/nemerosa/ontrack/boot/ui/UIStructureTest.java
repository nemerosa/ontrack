package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UIStructureTest extends AbstractITTestSupport {

    @Autowired
    private UIStructure structure;

    @Test
    public void createProject() {
        NameDescription nameDescription = nameDescription();
        Project project = structure.newProject(nameDescription);
        assertNotNull("Project not null", project);
        assertNotNull("Project ID not null", project.getId());
        assertTrue("Project ID set", project.getId().isSet());
        assertEquals("Project name", nameDescription.getName(), project.getName());
        assertEquals("Project description", nameDescription.getDescription(), project.getDescription());
    }

}
