package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.structure.EntityDataService;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.assertEquals;

public class EntityDataServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private EntityDataService entityDataService;

    @Test
    public void retrieve_authorized_for_view() throws Exception {
        // Creates an entity
        Project project = doCreateProject();
        // Key
        String key = uid("K");
        // Stores some data
        asAdmin().execute(() ->
                entityDataService.store(project, key, "Value 1")
        );
        // Retrieves it using view right only
        String value = asUser().withView(project).call(() ->
                entityDataService.retrieve(project, key).orElse(null)
        );
        // Cheks
        assertEquals("Value 1", value);
    }

}
