package net.nemerosa.ontrack.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonUtilsTest {

    @Test
    public void get_no_field_to_null() throws Exception {
        assertNull(JsonUtils.get(JsonUtils.object().end(), "name", null));
    }

    @Test
    public void get_null_field_to_null() throws Exception {
        assertNull(JsonUtils.get(JsonUtils.object().with("name", (String) null).end(), "name", null));
    }

    @Test
    public void get_field_to_null() throws Exception {
        assertEquals("Name", JsonUtils.get(JsonUtils.object().with("name", "Name").end(), "name", null));
    }
}