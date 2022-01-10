package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.it.AbstractServiceTestJUnit4Support;
import net.nemerosa.ontrack.model.support.StorageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

public class StorageServiceIT extends AbstractServiceTestJUnit4Support {

    @Autowired
    private StorageService storageService;

    @Test
    public void store_type_none() {
        Optional<StoredValue> retrieved = storageService.retrieve(uid("C"), "1", StoredValue.class);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void store_type() {
        StoredValue stored = new StoredValue("test");
        String store = uid("C");
        storageService.store(store, "1", stored);
        Optional<StoredValue> retrieved = storageService.retrieve(store, "1", StoredValue.class);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertEquals(stored, retrieved.get());
    }

    @Test
    public void store_Data() {
        StoredValue stored1 = new StoredValue("test-1");
        StoredValue stored2 = new StoredValue("test-2");
        String store = uid("C");

        storageService.store(store, "1", stored1);
        storageService.store(store, "2", stored2);

        Map<String, StoredValue> data = storageService.getData(store, StoredValue.class);

        assertEquals(2, data.size());
        assertEquals(stored1, data.get("1"));
        assertEquals(stored2, data.get("2"));
    }

}
