package net.nemerosa.ontrack.common;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void safeRegexMatch_null() {
        assertFalse(Utils.safeRegexMatch(null, null));
        assertFalse(Utils.safeRegexMatch(null, ""));
        assertFalse(Utils.safeRegexMatch(null, "abc"));
    }

    @Test
    public void safeRegexMatch_empty() {
        assertFalse(Utils.safeRegexMatch("", null));
        assertFalse(Utils.safeRegexMatch("", ""));
        assertFalse(Utils.safeRegexMatch("", "abc"));
    }

    @Test
    public void safeRegexMatch_blank() {
        assertFalse(Utils.safeRegexMatch(" ", null));
        assertFalse(Utils.safeRegexMatch(" ", ""));
        assertFalse(Utils.safeRegexMatch(" ", "abc"));
    }

    @Test
    public void safeRegexMatch_correct() {
        assertFalse(Utils.safeRegexMatch("ab.*", null));
        assertFalse(Utils.safeRegexMatch("ab.*", ""));
        assertTrue(Utils.safeRegexMatch("ab.*", "abc"));
        assertFalse(Utils.safeRegexMatch("ab.*", "acb"));
    }

    @Test
    public void safeRegexMatch_incorrect() {
        assertFalse(Utils.safeRegexMatch("ab.*)", null));
        assertFalse(Utils.safeRegexMatch("ab.*)", ""));
        assertFalse(Utils.safeRegexMatch("ab.*)", "abc"));
        assertFalse(Utils.safeRegexMatch("ab.*)", "acb"));
    }

    @Test
    public void asList_null() {
        assertTrue(Utils.asList(null).isEmpty());
    }

    @Test
    public void asList_blank() {
        assertTrue(Utils.asList("").isEmpty());
    }

    @Test
    public void asList_one() {
        assertEquals(
                Arrays.asList("Test"),
                Utils.asList("Test")
        );
    }

    @Test
    public void asList_two() {
        assertEquals(
                Arrays.asList("Test", "Second"),
                Utils.asList("Test\nSecond")
        );
    }

}