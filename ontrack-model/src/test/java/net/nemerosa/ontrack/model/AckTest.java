package net.nemerosa.ontrack.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AckTest {

    @Test
    public void ok() {
        assertTrue(Ack.OK.isSuccess());
    }

    @Test
    public void nok() {
        assertFalse(Ack.NOK.isSuccess());
    }

    @Test
    public void validate_true() {
        assertTrue(Ack.validate(true).isSuccess());
    }

    @Test
    public void validate_false() {
        assertFalse(Ack.validate(false).isSuccess());
    }

    @Test
    public void one_0() {
        assertFalse(Ack.one(0).isSuccess());
    }

    @Test
    public void one_1() {
        assertTrue(Ack.one(1).isSuccess());
    }

    @Test
    public void one_more() {
        assertFalse(Ack.one(2).isSuccess());
    }

    @Test
    public void to_json() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.create();
        String value = mapper.writeValueAsString(Ack.OK);
        assertEquals("{\"success\":true}", value);
    }

    @Test
    public void and() {
        assertFalse(Ack.NOK.and(Ack.NOK).isSuccess());
        assertFalse(Ack.NOK.and(Ack.OK).isSuccess());
        assertFalse(Ack.OK.and(Ack.NOK).isSuccess());
        assertTrue(Ack.OK.and(Ack.OK).isSuccess());
    }

    @Test
    public void or() {
        assertFalse(Ack.NOK.or(Ack.NOK).isSuccess());
        assertTrue(Ack.NOK.or(Ack.OK).isSuccess());
        assertTrue(Ack.OK.or(Ack.NOK).isSuccess());
        assertTrue(Ack.OK.or(Ack.OK).isSuccess());
    }

}
