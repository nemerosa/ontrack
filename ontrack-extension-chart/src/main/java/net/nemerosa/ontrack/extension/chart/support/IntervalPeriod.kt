package net.nemerosa.ontrack.extension.chart.support

import net.nemerosa.ontrack.model.exceptions.InputException
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

sealed interface IntervalPeriod {

    fun addTo(start: LocalDateTime): LocalDateTime
    fun format(start: LocalDateTime): String

    companion object
}

data class IntervalDatePeriod(
    private val period: Period,
) : IntervalPeriod {
    override fun addTo(start: LocalDateTime): LocalDateTime =
        start.plus(period)

    override fun format(start: LocalDateTime): String =
        start.format(DateTimeFormatter.ISO_DATE)
}

data class IntervalTimePeriod(
    private val duration: Duration,
) : IntervalPeriod {
    override fun addTo(start: LocalDateTime): LocalDateTime =
        start.plus(duration)

    override fun format(start: LocalDateTime): String =
        start.format(DateTimeFormatter.ISO_DATE_TIME)
}


fun parseIntervalPeriod(value: String): IntervalPeriod {
    val fixed = "(\\d+)([hdwmy])".toRegex()
    val f = fixed.matchEntire(value)
    return if (f != null) {
        val count = f.groupValues[1].toLong()
        val type = f.groupValues[2]
        when (type) {
            "h" -> IntervalTimePeriod(Duration.ofHours(count))
            "d" -> IntervalDatePeriod(Period.ofDays(count.toInt()))
            "w" -> IntervalDatePeriod(Period.ofWeeks(count.toInt()))
            "m" -> IntervalDatePeriod(Period.ofMonths(count.toInt()))
            "y" -> IntervalDatePeriod(Period.ofYears(count.toInt()))
            else -> throw IntervalPeriodFormatException("Unknow period type $type in $value")
        }
    } else {
        throw IntervalPeriodFormatException("Cannot parse period: $value")
    }
}

class IntervalPeriodFormatException(message: String) : InputException(message)