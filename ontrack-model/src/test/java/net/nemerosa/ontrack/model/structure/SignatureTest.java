package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SignatureTest {

    @Test
    public void testOf() {
        Signature s = Signature.of("Test");
        assertNotNull(s);
        assertNotNull(s.getTime());
        assertNotNull(s.getUser());
        assertEquals("Test", s.getUser().getName());
    }

    @Test
    public void testOfWithDateTime() {
        Signature s = Signature.of(TestUtils.dateTime(), "Test");
        assertNotNull(s);
        assertEquals(TestUtils.dateTime(), s.getTime());
        assertNotNull(s.getUser());
        assertEquals("Test", s.getUser().getName());
    }
}