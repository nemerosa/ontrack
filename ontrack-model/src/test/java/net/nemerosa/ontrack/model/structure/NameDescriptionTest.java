package net.nemerosa.ontrack.model.structure;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NameDescriptionTest {

    @Test
    public void nameValidation() {
        assertTrue(Pattern.matches(NameDescription.NAME, "Test"));
        assertTrue(Pattern.matches(NameDescription.NAME, "2"));
        assertTrue(Pattern.matches(NameDescription.NAME, "2.0.0"));
        assertTrue(Pattern.matches(NameDescription.NAME, "2.0.0-alpha"));
        assertTrue(Pattern.matches(NameDescription.NAME, "2.0.0-alpha-1"));
        assertTrue(Pattern.matches(NameDescription.NAME, "2.0.0-alpha-1-14"));
        assertFalse(Pattern.matches(NameDescription.NAME, "2.0.0-alpha 1-14"));
        assertTrue(Pattern.matches(NameDescription.NAME, "TEST"));
        assertTrue(Pattern.matches(NameDescription.NAME, "TEST_1"));
        assertTrue(Pattern.matches(NameDescription.NAME, "TEST_ONE"));
        assertFalse(Pattern.matches(NameDescription.NAME, "TEST ONE"));
    }

}
