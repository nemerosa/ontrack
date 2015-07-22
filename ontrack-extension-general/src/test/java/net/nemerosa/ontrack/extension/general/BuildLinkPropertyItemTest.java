package net.nemerosa.ontrack.extension.general;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BuildLinkPropertyItemTest {

    @Test
    public void match_name_nok() {
        assertFalse(
                BuildLinkPropertyItem.of("name", "value").match("nam", "value")
        );
    }

    @Test
    public void match_value_nok_with_exact_value() {
        assertFalse(
                BuildLinkPropertyItem.of("name", "value").match("name", "val")
        );
    }

    @Test
    public void match_value_ok_with_exact_value() {
        assertTrue(
                BuildLinkPropertyItem.of("name", "value").match("name", "value")
        );
    }

    @Test
    public void match_value_ok_with_pattern() {
        assertTrue(
                BuildLinkPropertyItem.of("name", "value").match("name", "val*")
        );
    }

    @Test
    public void match_value_ok_with_wildcard() {
        assertTrue(
                BuildLinkPropertyItem.of("name", "value").match("name", "*")
        );
    }

    @Test
    public void match_value_ok_with_blank() {
        assertTrue(
                BuildLinkPropertyItem.of("name", "value").match("name", "")
        );
    }

    @Test
    public void match_value_ok_with_null() {
        assertTrue(
                BuildLinkPropertyItem.of("name", "value").match("name", null)
        );
    }

}
