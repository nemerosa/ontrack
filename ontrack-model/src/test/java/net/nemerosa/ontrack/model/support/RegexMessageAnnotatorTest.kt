package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.of
import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.t
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RegexMessageAnnotatorTest {

    private val annotationFunction = { match: String -> of("test").attr("match", match) }

    @Test
    fun simple_no_match() {
        val annotations = RegexMessageAnnotator(
            SIMPLE_REGEX,
            annotationFunction
        ).annotate("No match here.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                t("No match here.")
            ),
            annotations
        )
    }

    @Test
    fun simple_one_match() {
        val annotations = RegexMessageAnnotator(
            SIMPLE_REGEX,
            annotationFunction
        ).annotate("#177 match here.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                annotationFunction("#177"),
                t(" match here.")
            ),
            annotations
        )
    }

    @Test
    fun simple_several_matches() {
        val annotations = RegexMessageAnnotator(
            SIMPLE_REGEX,
            annotationFunction
        ).annotate("#177 #178 matches.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                annotationFunction("#177"),
                t(" "),
                annotationFunction("#178"),
                t(" matches.")
            ),
            annotations
        )
    }

    @Test
    fun simple_identical_matches() {
        val annotations = RegexMessageAnnotator(
            SIMPLE_REGEX,
            annotationFunction
        ).annotate("#177 #177 matches.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                annotationFunction("#177"),
                t(" "),
                annotationFunction("#177"),
                t(" matches.")
            ),
            annotations
        )
    }

    @Test
    fun jira_no_match() {
        val annotations = RegexMessageAnnotator(
            JIRA_REGEX,
            annotationFunction
        ).annotate("No match here.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                t("No match here.")
            ),
            annotations
        )
    }

    @Test
    fun jira_one_match() {
        val annotations = RegexMessageAnnotator(
            JIRA_REGEX,
            annotationFunction
        ).annotate("TEST-111 match here.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                annotationFunction("TEST-111"),
                t(" match here.")
            ),
            annotations
        )
    }

    @Test
    fun jira_several_matches() {
        val annotations = RegexMessageAnnotator(
            JIRA_REGEX,
            annotationFunction
        ).annotate("JA2CP-11: Message with (TEST-111), RG-12 and TPM-123")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                annotationFunction("JA2CP-11"),
                t(": Message with ("),
                annotationFunction("TEST-111"),
                t("), "),
                annotationFunction("RG-12"),
                t(" and "),
                annotationFunction("TPM-123"),
            ),
            annotations
        )
    }

    @Test
    fun jira_identical_matches() {
        val annotations = RegexMessageAnnotator(
            JIRA_REGEX,
            annotationFunction
        ).annotate("TMP-122 matches TMP-122.")
        assertNotNull(annotations)
        assertEquals(
            listOf(
                annotationFunction("TMP-122"),
                t(" matches "),
                annotationFunction("TMP-122"),
                t(".")
            ),
            annotations
        )
    }

    companion object {
        val SIMPLE_REGEX = "(#\\d+)".toRegex()
        val JIRA_REGEX = "(?:[^A-Z0-9]|^)([A-Z][A-Z0-9]+-\\d+)(?:[^0-9]|\$)".toRegex()
    }
}
