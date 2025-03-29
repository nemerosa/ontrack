package net.nemerosa.ontrack.json;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    public void get_date_null() throws Exception {
        assertNull(JsonUtils.getDate(JsonUtils.object().withNull("data").end(), "data", null));
    }

    @Test
    public void get_string_list_no_field() {
        assertNull(JsonUtils.getStringList(JsonUtils.object().end(), "test"));
    }

    @Test
    public void get_string_list_null_field() {
        assertEquals(
                Arrays.<String>asList(),
                JsonUtils.getStringList(JsonUtils.object().with("test", (String) null).end(), "test")
        );
    }

    @Test
    public void get_string_list_one_value() {
        assertEquals(
                Arrays.asList("One"),
                JsonUtils.getStringList(JsonUtils.object().with("test", JsonUtils.stringArray("One")).end(), "test")
        );
    }

    @Test
    public void get_string_list_two_values() {
        assertEquals(
                Arrays.asList("One", "Two"),
                JsonUtils.getStringList(JsonUtils.object().with("test", JsonUtils.stringArray("One", "Two")).end(), "test")
        );
    }
}