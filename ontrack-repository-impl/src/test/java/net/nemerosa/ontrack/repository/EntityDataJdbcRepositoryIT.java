package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class EntityDataJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private EntityDataRepository repository;

    private Project project;

    @Before
    public void create_project() {
        project = do_create_project();
    }

    @Test
    public void save_retrieve_delete_data() {
        String key = "Test 1";
        assertNotNull(repository.retrieve(project, key));

        repository.store(project, key, "Value 1");
        assertEquals(repository.retrieve(project, key), "Value 1");

        repository.delete(project, key);
        assertNull(repository.retrieve(project, key));
    }

    @Test
    public void save_update_data() {
        String key = "Test 2";
        assertNull(repository.retrieve(project, key));

        repository.store(project, key, "Value 1");
        assertEquals(repository.retrieve(project, key), "Value 1");

        repository.store(project, key, "Value 2");
        assertEquals(repository.retrieve(project, key), "Value 2");
    }

    @Test
    public void save_update_json_data() {
        String key = "Test 3";
        assertNull(repository.retrieveJson(project, key));

        repository.storeJson(project, key, JsonUtils.format(new TestObject("Value 1")));
        assertEquals(new TestObject("Value 1"), JsonUtils.parse(repository.retrieveJson(project, key), TestObject.class));

        repository.storeJson(project, key, JsonUtils.format(new TestObject("Value 2")));
        assertEquals(new TestObject("Value 2"), JsonUtils.parse(repository.retrieveJson(project, key), TestObject.class));
    }

}
