package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static net.nemerosa.ontrack.test.TestUtils.assertJsonEquals;
import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

@Transactional
public class StorageJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private StorageRepository repository;

    @Test
    public void no_data_returns_empty() {
        Optional<JsonNode> o = repository.retrieveJson(uid("C"), "1");
        assertNotNull(o);
        assertFalse(o.isPresent());
    }

    @Test
    public void store_and_retrieve() throws JsonProcessingException {
        ObjectNode json = JsonUtils.object().with("name", "My name").end();

        String store = uid("C");

        repository.storeJson(store, "1", json);

        Optional<JsonNode> o = repository.retrieveJson(store, "1");
        assertNotNull(o);
        assertTrue(o.isPresent());
        assertJsonEquals(json, o.get());
    }

    @Test
    public void store_and_delete() throws JsonProcessingException {
        ObjectNode json = JsonUtils.object().with("name", "My name").end();

        String store = uid("C");
        repository.storeJson(store, "1", json);
        repository.storeJson(store, "1", null);

        Optional<JsonNode> o = repository.retrieveJson(store, "1");
        assertNotNull(o);
        assertFalse(o.isPresent());
    }

}
