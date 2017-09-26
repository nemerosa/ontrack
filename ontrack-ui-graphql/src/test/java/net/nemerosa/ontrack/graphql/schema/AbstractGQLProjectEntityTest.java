package net.nemerosa.ontrack.graphql.schema;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.structure.Signature;
import org.junit.Test;

import java.time.LocalDateTime;

import static net.nemerosa.ontrack.graphql.schema.GQLTypeCreation.getCreationFromSignature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AbstractGQLProjectEntityTest {

    @Test
    public void signature_to_map() {
        LocalDateTime now = Time.now();
        GQLTypeCreation.Creation map = getCreationFromSignature(Signature.of(now, "test"));
        assertEquals("test", map.getUser());
        assertEquals(Time.forStorage(now), map.getTime());
    }

    @Test
    public void no_signature_to_map() {
        GQLTypeCreation.Creation map = getCreationFromSignature(Signature.of(null, null));
        assertNull(map.getUser());
        assertNull(map.getTime());
    }

    @Test
    public void null_signature_to_map() {
        GQLTypeCreation.Creation map = getCreationFromSignature(null);
        assertNull(map.getUser());
        assertNull(map.getTime());
    }

}
