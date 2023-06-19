package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.of
import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.t
import org.junit.jupiter.api.Test
import java.util.function.Function
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RegexMessageAnnotatorTest {

    private val annotationFunction = Function { match: String? -> of("test").attr("match", match!!) }

    @Test
    fun no_match() {
        val annotations = RegexMessageAnnotator(REGEX, annotationFunction).annotate("No match here.")
        assertNotNull(annotations)
        assertEquals(
                listOf(
                        t("No match here.")
                ),
                annotations)
    }

    @Test
    fun one_match() {
        val annotations = RegexMessageAnnotator(REGEX, annotationFunction).annotate("#177 match here.")
        assertNotNull(annotations)
        assertEquals(
                listOf(
                        annotationFunction.apply("#177"),
                        t(" match here.")
                ),
                annotations)
    }

    @Test
    fun several_matches() {
        val annotations = RegexMessageAnnotator(REGEX, annotationFunction).annotate("#177 #178 matches.")
        assertNotNull(annotations)
        assertEquals(
                listOf(
                        annotationFunction.apply("#177"),
                        t(" "),
                        annotationFunction.apply("#178"),
                        t(" matches.")
                ),
                annotations)
    }

    @Test
    fun identical_matches() {
        val annotations = RegexMessageAnnotator(REGEX, annotationFunction).annotate("#177 #177 matches.")
        assertNotNull(annotations)
        assertEquals(
                listOf(
                        annotationFunction.apply("#177"),
                        t(" "),
                        annotationFunction.apply("#177"),
                        t(" matches.")
                ),
                annotations)
    }

    companion object {
        const val REGEX = "(#\\d+)"
    }
}
