package net.nemerosa.ontrack.model.structure;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.nemerosa.ontrack.model.structure.Replacement.replacementFn;
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

    protected static String applyReplacements(final String value, List<Replacement> replacements) {
        return replacementFn(replacements).apply(value);
    }

    @Test
    public void applyReplacements_none() {
        assertEquals("branches/11.7", applyReplacements("branches/11.7", Collections.emptyList()));
    }

    @Test
    public void applyReplacements_null() {
        assertEquals("branches/11.7", applyReplacements("branches/11.7", Arrays.asList(
                new Replacement(null, "any")
        )));
    }

    @Test
    public void applyReplacements_blank() {
        assertEquals("branches/11.7", applyReplacements("branches/11.7", Arrays.asList(
                new Replacement("", "any")
        )));
    }

    @Test
    public void applyReplacements_direct() {
        assertEquals("branches/11.8", applyReplacements("branches/11.7", Arrays.asList(
                new Replacement("11.7", "11.8")
        )));
    }

    @Test
    public void applyReplacements_several() {
        assertEquals("Release pipeline for branches/11.7", applyReplacements("Pipeline for trunk", Arrays.asList(
                new Replacement("trunk", "branches/11.7"),
                new Replacement("Pipeline", "Release pipeline")
        )));
    }
}