package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;
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

    @Test
    public void new_form() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("fields", array()
                                .with(object()
                                        .with("name", "name")
                                        .with("label", "Name")
                                        .with("length", 40)
                                        .with("regex", "[A-Za-z0-9_\\.\\-]+")
                                        .end())
                                .with(object()
                                        .with("name", "description")
                                        .with("label", "Description")
                                        .with("length", 500)
                                        .with("rows", 3)
                                        .end())
                                .end())
                        .end(),
                Project.form()
        );
    }

}
