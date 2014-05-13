package net.nemerosa.ontrack.model.structure;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectTest {

    @Test
    public void of() {
        Project p = Project.of(new NameDescription("PRJ", "Project"));
        assertNotNull(p);
        assertNotNull(p.getId());
        assertFalse(p.getId().isSet());
        assertEquals("PRJ", p.getName());
        assertEquals("Project", p.getDescription());
    }

}
