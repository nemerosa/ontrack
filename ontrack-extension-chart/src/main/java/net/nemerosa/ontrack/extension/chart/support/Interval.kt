package net.nemerosa.ontrack.extension.chart.support

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.exceptions.InputException
import java.time.LocalDateTime

data class Interval(
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    companion object {

        private val fixed = "(\\d+)([dwmy])".toRegex()

        fun parse(value: String, ref: LocalDateTime = Time.now()): Interval {
            val f = fixed.matchEntire(value)
            return if (f != null) {
                val count = f.groupValues[1].toLong()
                val type = f.groupValues[2]
                fixed(value, count, type, ref)
            } else {
                TODO()
            }
        }

        private fun fixed(value: String, count: Long, type: String, ref: LocalDateTime): Interval {
            val start = when (type) {
                "d" -> ref.minusDays(count)
                "w" -> ref.minusWeeks(count)
                "m" -> ref.minusMonths(count)
                "y" -> ref.minusYears(count)
                else -> throw IntervalFormatException("Unknown time interval [$type] in $value")
            }
            return Interval(start, ref)
        }
    }
}

class IntervalFormatException(message: String) : InputException(message)
