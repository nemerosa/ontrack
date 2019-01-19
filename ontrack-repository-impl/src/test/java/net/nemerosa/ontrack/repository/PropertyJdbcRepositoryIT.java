package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static net.nemerosa.ontrack.test.TestUtils.assertJsonEquals;
import static org.junit.Assert.*;

@Transactional
public class PropertyJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private PropertyRepository repository;

    private Project project;

    private static final String PROPERTY_TYPE = "test";

    @Before
    public void create_project() {
        project = do_create_project();
    }

    @Test
    public void search_key_truncated_when_too_big() {
        String veryLongSearchKey = StringUtils.repeat("m", 601);
        repository.saveProperty(
                "net.nemerosa.ontrack.repository.PropertyJdbcRepositoryIT",
                ProjectEntityType.PROJECT,
                project.getId(),
                JsonUtils.object().with("value", "test").end(),
                veryLongSearchKey
        );
        // Gets the property back
        TProperty property = repository.loadProperty(
                "net.nemerosa.ontrack.repository.PropertyJdbcRepositoryIT",
                ProjectEntityType.PROJECT,
                project.getId()
        );
        assertNotNull(property);
        String storedSearchKey = property.getSearchKey();
        assertEquals(600, storedSearchKey.length());
    }

    @Test
    public void save_retrieve_delete_property() throws JsonProcessingException {
        assertNull(repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId()));

        repository.saveProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId(), JsonUtils.object().with("value", 10).end(), "10");
        TProperty t = repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId());
        assertNotNull(t);
        assertJsonEquals(
                JsonUtils.object().with("value", 10).end(),
                t.getJson()
        );

        repository.deleteProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId());
        assertNull(repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId()));
    }

    @Test
    public void save_update_data() throws JsonProcessingException {
        assertNull(repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId()));

        repository.saveProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId(), JsonUtils.object().with("value", 10).end(), "10");
        TProperty t = repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId());
        assertNotNull(t);
        assertJsonEquals(
                JsonUtils.object().with("value", 10).end(),
                t.getJson()
        );

        repository.saveProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId(), JsonUtils.object().with("value", 12).end(), "12");
        t = repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.getId());
        assertNotNull(t);
        assertJsonEquals(
                JsonUtils.object().with("value", 12).end(),
                t.getJson()
        );
    }

}
