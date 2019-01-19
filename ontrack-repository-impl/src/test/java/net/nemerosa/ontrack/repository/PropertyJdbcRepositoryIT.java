package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
public class PropertyJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private PropertyRepository repository;

    private Project project;

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

}
