package net.nemerosa.ontrack.extension.general;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaInfoPropertyItemTest {

    @Test
    public void match_name_nok() {
        assertFalse(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("nam", "value")
        );
    }

    @Test
    public void match_value_nok_with_exact_value() {
        assertFalse(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("name", "val")
        );
    }

    @Test
    public void match_value_ok_with_exact_value() {
        assertTrue(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("name", "value")
        );
    }

    @Test
    public void match_value_ok_with_pattern() {
        assertTrue(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("name", "val*")
        );
    }

    @Test
    public void match_value_ok_with_wildcard() {
        assertTrue(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("name", "*")
        );
    }

    @Test
    public void match_value_ok_with_blank() {
        assertTrue(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("name", "")
        );
    }

    @Test
    public void match_value_ok_with_null() {
        assertTrue(
                new MetaInfoPropertyItem("name", "value", "").matchNameValue("name", null)
        );
    }

}
