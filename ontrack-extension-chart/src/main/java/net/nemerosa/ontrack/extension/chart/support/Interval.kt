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
        private val explicit =
            "(\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d)-(\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d)".toRegex()

        fun parse(value: String, ref: LocalDateTime = Time.now()): Interval {
            val f = fixed.matchEntire(value)
            return if (f != null) {
                val count = f.groupValues[1].toLong()
                val type = f.groupValues[2]
                fixed(value, count, type, ref)
            } else {
                val x = explicit.matchEntire(value)
                if (x != null) {
                    val start = Time.fromStorage(x.groupValues[1])
                        ?: throw IntervalFormatException("Cannot parse interval start date: ${x.groupValues[1]}")
                    val end = Time.fromStorage(x.groupValues[2])
                        ?: throw IntervalFormatException("Cannot parse interval start date: ${x.groupValues[2]}")
                    Interval(start, end)
                } else {
                    throw IntervalFormatException("Cannot parse interval: $value")
                }
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
