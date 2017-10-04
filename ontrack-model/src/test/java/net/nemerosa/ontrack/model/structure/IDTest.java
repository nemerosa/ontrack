package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.IntNode;
import net.nemerosa.ontrack.json.JsonUtils;
import org.junit.Test;

import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;
import static org.junit.Assert.*;

public class IDTest {

    @Test
    public void none() {
        ID id = ID.NONE;
        assertNotNull(id);
        assertFalse(id.isSet());
        assertEquals(0, id.getValue());
        assertEquals("0", id.toString());
    }

    @Test
    public void set() {
        ID id = ID.of(1);
        assertNotNull(id);
        assertTrue(id.isSet());
        assertEquals(1, id.getValue());
        assertEquals("1", id.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_zero() {
        ID.of(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_negative() {
        ID.of(-1);
    }

    @Test
    public void set_to_json() throws JsonProcessingException {
        assertJsonWrite(
                JsonUtils.number(12),
                ID.of(12)
        );
    }

    @Test
    public void read_from_json() throws JsonProcessingException {
        assertJsonRead(
                ID.of(9),
                new IntNode(9),
                ID.class
        );
    }

    @Test
    public void unset_to_json() throws JsonProcessingException {
        assertJsonWrite(
                JsonUtils.number(0),
                ID.NONE
        );
    }

    @Test
    public void is_defined_null() {
        assertFalse(ID.isDefined(null));
    }

    @Test
    public void is_defined_none() {
        assertFalse(ID.isDefined(ID.NONE));
    }

    @Test
    public void is_defined_set() {
        assertTrue(ID.isDefined(ID.of(1)));
    }

    @Test
    public void if_set_ok() {
        assertEquals("1", ID.of(1).ifSet(String::valueOf).get());
        assertEquals("1", ID.of(1).ifSet(String::valueOf).orElse("-"));
    }

    @Test
    public void if_set_ok_null() {
        assertFalse(ID.of(1).ifSet(value -> null).isPresent());
    }

    @Test
    public void if_set_nok() {
        assertFalse(ID.NONE.ifSet(String::valueOf).isPresent());
        assertEquals("-", ID.NONE.ifSet(String::valueOf).orElse("-"));
    }

}
