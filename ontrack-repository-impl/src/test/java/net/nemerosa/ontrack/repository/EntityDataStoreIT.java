package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.IntNode;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.repository.support.store.EntityDataStore;
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static net.nemerosa.ontrack.test.TestUtils.assertJsonEquals;
import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

/**
 * Integration tests for {@link EntityDataStore}.
 */
public class EntityDataStoreIT extends AbstractRepositoryTestSupport {

    @Autowired
    private EntityDataStore store;

    @Test
    public void add() throws JsonProcessingException {
        // Entity
        Branch branch = do_create_branch();
        // Adds some data
        String name = uid("T");
        EntityDataStoreRecord record = store.add(branch, "TEST", name, Signature.of("test"), null, new IntNode(15));
        // Checks
        assertNotNull(record);
        assertTrue(record.getId() > 0);
        assertEquals("TEST", record.getCategory());
        assertNotNull(record.getSignature());
        assertEquals(name, record.getName());
        assertNull(record.getGroupName());
        assertEquals(branch.getProjectEntityType(), record.getEntity().getProjectEntityType());
        assertEquals(branch.getId(), record.getEntity().getId());
        assertJsonEquals(
                new IntNode(15),
                record.getData()
        );
    }

}
