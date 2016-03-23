package net.nemerosa.ontrack.service.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.support.StorageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static net.nemerosa.ontrack.test.TestUtils.assertJsonEquals;
import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

public class StorageServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private StorageService storageService;

    @Test
    public void no_data_returns_empty() {
        Optional<JsonNode> o = storageService.retrieveJson(uid("C"), "1");
        assertNotNull(o);
        assertFalse(o.isPresent());
    }

    @Test
    public void store_and_retrieve() throws JsonProcessingException {
        ObjectNode json = JsonUtils.object().with("name", "My name").end();

        String store = uid("C");

        storageService.storeJson(store, "1", json);

        Optional<JsonNode> o = storageService.retrieveJson(store, "1");
        assertNotNull(o);
        assertTrue(o.isPresent());
        assertJsonEquals(json, o.get());
    }

    @Test
    public void store_and_delete() throws JsonProcessingException {
        ObjectNode json = JsonUtils.object().with("name", "My name").end();

        String store = uid("C");
        storageService.storeJson(store, "1", json);
        storageService.storeJson(store, "1", null);

        Optional<JsonNode> o = storageService.retrieveJson(store, "1");
        assertNotNull(o);
        assertFalse(o.isPresent());
    }

}
