package net.nemerosa.ontrack.model.structure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReplacementTest {

    @Test
    public void replace_null() {
        assertNull(
                new Replacement("test", "new").replace(null)
        );
    }

    @Test
    public void replace_empty() {
        assertEquals(
                "",
                new Replacement("test", "new").replace("")
        );
    }

    @Test
    public void replace_blank() {
        assertEquals(
                "  ",
                new Replacement("test", "new").replace("  ")
        );
    }

    @Test
    public void replace_whole() {
        assertEquals(
                "new",
                new Replacement("test", "new").replace("test")
        );
    }

    @Test
    public void replace_partial() {
        assertEquals(
                "new project",
                new Replacement("test", "new").replace("test project")
        );
    }

    @Test
    public void replace_several() {
        assertEquals(
                "new of news",
                new Replacement("test", "new").replace("test of tests")
        );
    }

    @Test
    public void replace_no_regex() {
        assertEquals(
                "test",
                new Replacement("", "new").replace("test")
        );
    }

    @Test
    public void replace_no_replacement() {
        assertEquals(
                "test",
                new Replacement("test", "").replace("test")
        );
    }
}