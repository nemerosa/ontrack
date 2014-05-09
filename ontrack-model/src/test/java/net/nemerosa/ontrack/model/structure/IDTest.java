package net.nemerosa.ontrack.model.structure;

import org.junit.Test;

import static org.junit.Assert.*;

public class IDTest {

    @Test
    public void none() {
        ID id = ID.NONE;
        assertNotNull(id);
        assertFalse(id.isSet());
        assertNull(id.getValue());
        assertNull(id.toString());
    }

    @Test
    public void set() {
        ID id = ID.of("test");
        assertNotNull(id);
        assertTrue(id.isSet());
        assertEquals("test", id.getValue());
        assertEquals("test", id.toString());
    }

    @Test(expected = NullPointerException.class)
    public void not_null() {
        ID.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_empty() {
        ID.of("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_blank() {
        ID.of("  ");
    }

}
