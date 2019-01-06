package net.nemerosa.ontrack.common

/**
 * Regular expression to validate a colour.
 */
const val RGB_COLOR_REGEX: String = "#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})"

data class RGBColor(
        val red: Int,
        val green: Int,
        val blue: Int
) {

    init {
        validateComponent(red)
        validateComponent(green)
        validateComponent(blue)
    }

    private fun validateComponent(component: Int) {
        if (component < 0) throw RGBColorException("Color component must be >= 0")
        if (component > 255) throw RGBColorException("Color component must be <= 255")
    }

    /**
     * Returns black, or white, depending on the color, in order to maximize the constrast.
     */
    fun toBlackOrWhite(): RGBColor {
        // Ref: http://www.niwa.nu/2013/05/math-behind-colorspace-conversions-rgb-hsl/
        val r = red / 255.0
        val g = green / 255.0
        val b = blue / 255.0

        val min = minOf(r, g, b)
        val max = maxOf(r, g, b)

        val l = (min + max) / 2

        return when {
            l <= 0.75 -> WHITE
            else -> BLACK
        }
    }

    override fun toString(): String {
        return "#${toHex(red)}${toHex(green)}${toHex(blue)}"
    }

    companion object {
        /**
         * Regex
         */
        private val regex = RGB_COLOR_REGEX.toRegex()

        /**
         * Black
         */
        val BLACK = RGBColor(0, 0, 0)

        /**
         * White
         */
        val WHITE = RGBColor(255, 255, 255)

        /**
         *
         */
        fun parse(text: String): RGBColor {
            val matchResult = regex.matchEntire(text)
            if (matchResult != null) {
                val (hr, hg, hb) = matchResult.destructured
                val r = fromHex(hr)
                val g = fromHex(hg)
                val b = fromHex(hb)
                return RGBColor(r, g, b)
            } else {
                throw RGBColorException("Color format does not match: $text")
            }
        }

        private fun fromHex(hex: String): Int {
            return hex.toInt(16)
        }

        private fun toHex(component: Int): String {
            val h = component.toString(16).toUpperCase()
            return if (component < 16) {
                "0$h"
            } else {
                h
            }
        }
    }

}

fun String.toRGBColor() = RGBColor.parse(this)

class RGBColorException(message: String) : IllegalArgumentException(message)
