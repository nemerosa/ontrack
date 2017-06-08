package net.nemerosa.ontrack.graphql.schema;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.structure.Signature;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static net.nemerosa.ontrack.graphql.schema.AbstractGQLProjectEntity.getMapFromSignature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AbstractGQLProjectEntityTest {

    @Test
    public void signature_to_map() {
        LocalDateTime now = Time.now();
        Map<String, String> map = getMapFromSignature(Signature.of(now, "test"));
        assertEquals("test", map.get("user"));
        assertEquals(Time.forStorage(now), map.get("time"));
    }

    @Test
    public void no_signature_to_map() {
        Map<String, String> map = getMapFromSignature(Signature.of(null, null));
        assertNull(map.get("user"));
        assertNull(map.get("time"));
    }

    @Test
    public void null_signature_to_map() {
        Map<String, String> map = getMapFromSignature(null);
        assertNull(map.get("user"));
        assertNull(map.get("time"));
    }

}
