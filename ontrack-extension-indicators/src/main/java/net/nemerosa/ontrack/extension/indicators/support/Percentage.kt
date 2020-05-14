package net.nemerosa.ontrack.extension.indicators.support

/**
 * Representation of a value strictly between 0 and 100.
 */
data class Percentage(
        val value: Int
) {

    init {
        check(value in 0..100) { "Value must be >=0 and <= 100" }
    }

    override fun toString(): String = "$value%"

    fun invert() = Percentage(100 - value)

    operator fun compareTo(percent: Percentage): Int =
            this.value.compareTo(percent.value)
}

fun Int.percent() = Percentage(this)
