package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.structure.Replacement.Companion.replacementFn
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class ReplacementTest {

    @Test
    fun replace_empty() {
        assertEquals(
            "",
            Replacement("test", "new").replace("")
        )
    }

    @Test
    fun replace_blank() {
        assertEquals(
            "  ",
            Replacement("test", "new").replace("  ")
        )
    }

    @Test
    fun replace_whole() {
        assertEquals(
            "new",
            Replacement("test", "new").replace("test")
        )
    }

    @Test
    fun replace_partial() {
        assertEquals(
            "new project",
            Replacement("test", "new").replace("test project")
        )
    }

    @Test
    fun replace_several() {
        assertEquals(
            "new of news",
            Replacement("test", "new").replace("test of tests")
        )
    }

    @Test
    fun replace_no_regex() {
        assertEquals(
            "test",
            Replacement("", "new").replace("test")
        )
    }

    @Test
    fun replace_no_replacement() {
        assertEquals(
            "test",
            Replacement("test", "").replace("test")
        )
    }

    @Test
    fun applyReplacements_none() {
        assertEquals("branches/11.7", applyReplacements("branches/11.7", emptyList()))
    }

    @Test
    fun applyReplacements_blank() {
        assertEquals(
            "branches/11.7", applyReplacements(
                "branches/11.7", listOf(
                    Replacement("", "any")
                )
            )
        )
    }

    @Test
    fun applyReplacements_direct() {
        assertEquals(
            "branches/11.8", applyReplacements(
                "branches/11.7", listOf(
                    Replacement("11.7", "11.8")
                )
            )
        )
    }

    @Test
    fun applyReplacements_several() {
        assertEquals(
            "Release pipeline for branches/11.7", applyReplacements(
                "Pipeline for trunk", listOf(
                    Replacement("trunk", "branches/11.7"),
                    Replacement("Pipeline", "Release pipeline")
                )
            )
        )
    }

    companion object {
        fun applyReplacements(value: String, replacements: List<Replacement>): String {
            return replacementFn(replacements)(value)
        }
    }
}