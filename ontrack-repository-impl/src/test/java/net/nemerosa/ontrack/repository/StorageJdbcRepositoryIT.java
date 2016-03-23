package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
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

    @Test
    public void get_keys() {
        String store = uid("C");
        repository.storeJson(store, "1", JsonUtils.object().with("name", "1").end());
        repository.storeJson(store, "2", JsonUtils.object().with("name", "2").end());
        assertEquals(
                Arrays.asList("1", "2"),
                repository.getKeys(store)
        );
    }

    @Test
    public void get_data() throws JsonProcessingException {
        String store = uid("C");
        ObjectNode data1 = JsonUtils.object().with("name", "1").end();
        ObjectNode data2 = JsonUtils.object().with("name", "2").end();

        repository.storeJson(store, "1", data1);
        repository.storeJson(store, "2", data2);

        Map<String, JsonNode> data = repository.getData(store);

        assertEquals(2, data.size());
        assertJsonEquals(data1, data.get("1"));
        assertJsonEquals(data2, data.get("2"));
    }

}
