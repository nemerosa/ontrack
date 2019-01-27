package net.nemerosa.ontrack.common

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RGBColorTest {

    @Test
    fun parsing() {
        "#ff1111" convertsTo RGBColor(255, 17, 17)
        "#FF1111" convertsTo RGBColor(255, 17, 17)
        "ff1111".doesNotConvert()
        "f11".doesNotConvert()
        "white".doesNotConvert()
    }

    @Test
    fun `Black and white`() {
        RGBColor.BLACK blackAndWhiteTo RGBColor.WHITE
        RGBColor.WHITE blackAndWhiteTo RGBColor.BLACK

        "#ff1111" blackAndWhiteTo RGBColor.WHITE
        "#11ff11" blackAndWhiteTo RGBColor.WHITE
        "#1111ff" blackAndWhiteTo RGBColor.WHITE
        "#001111" blackAndWhiteTo RGBColor.WHITE
        "#110011" blackAndWhiteTo RGBColor.WHITE
        "#111100" blackAndWhiteTo RGBColor.WHITE

        "#e0f6e4" blackAndWhiteTo RGBColor.BLACK
        "#0f44f8" blackAndWhiteTo RGBColor.WHITE
    }

    private fun String.doesNotConvert() {
        assertFailsWith<RGBColorException> {
            toRGBColor()
        }
    }

    private infix fun String.convertsTo(expected: RGBColor) {
        assertEquals(
                expected,
                toRGBColor(),
                "$this converts to RGBColor $expected"
        )
    }

    private infix fun String.blackAndWhiteTo(expected: RGBColor) {
        RGBColor.parse(this) blackAndWhiteTo expected
    }

    private infix fun RGBColor.blackAndWhiteTo(expected: RGBColor) {
        assertEquals(
                expected,
                this.toBlackOrWhite(),
                "$this in B/W is expected to be $expected"
        )
    }

}