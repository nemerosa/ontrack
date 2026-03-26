package net.nemerosa.ontrack.common

import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * Regex for 1800 - numeric, number of seconds
 */
private val durationNumericRegex = "\\d+".toRegex()

/**
 * Regex for PT1H - ISO representation
 */
private val durationIsoRegex = "P(?:\\d+Y)?(?:\\d+M)?(?:\\d+D)?(?:T(?:\\d+H)?(?:\\d+M)?(?:\\d+S)?)?".toRegex()

/**
 * Regex for 15d - shorthand representation, number of seconds (s), minutes (m), hours (h), days (d) or weeks (w)
 */
private val durationShorthandRegex = "(\\d+)([smhdwMy]|ms)".toRegex()

/**
 * Combination of all regexes for durations
 */
val durationRegex = "^${durationNumericRegex}|${durationIsoRegex}|${durationShorthandRegex}$"

/**
 * Week in seconds
 */
private val weekSeconds = 86400 * 7

/**
 * Day in seconds
 */
private val daySeconds = 24 * 3600

/**
 * Hour in seconds
 */
private val hourSeconds = 3600

/**
 * Minute in seconds
 */
private val minuteSeconds = 60

/**
 * Formatting of durations using shorthand representations when possible.
 */
fun Duration.format(): String {
    val millis = this.toMillis()
    return when {
        millis == 0L -> "0s"
        millis % (weekSeconds * 1000L) == 0L -> "${millis / (weekSeconds * 1000L)}w"
        millis % (daySeconds * 1000L) == 0L -> "${millis / (daySeconds * 1000L)}d"
        millis % (hourSeconds * 1000L) == 0L -> "${millis / (hourSeconds * 1000L)}h"
        millis % (minuteSeconds * 1000L) == 0L -> "${millis / (minuteSeconds * 1000L)}m"
        millis % 1000L == 0L -> "${millis / 1000L}s"
        else -> "${millis}ms"
    }
}

/**
 * Parsing of durations using different possible formats:
 *
 * * 1800 - numeric, number of seconds
 * * PT1H - ISO representation
 * * 15d - shorthand representation, number of seconds (s), minutes (m), hours (h), days (d), weeks (w) or milliseconds (ms)
 *
 * Returns null if not parsable.
 */
fun parseDuration(value: String): Duration? {
    val numeric = durationNumericRegex.matchEntire(value)
    if (numeric != null) {
        return Duration.ofSeconds(value.toLong())
    }
    val iso = durationIsoRegex.matchEntire(value)
    if (iso != null) {
        return Duration.parse(value)
    }
    val shorthand = durationShorthandRegex.matchEntire(value)
    if (shorthand != null) {
        val count = shorthand.groupValues[1].toLong()
        val unit = shorthand.groupValues[2]
        return when (unit) {
            "ms" -> Duration.ofMillis(count)
            "s" -> Duration.ofSeconds(count)
            "m" -> Duration.ofMinutes(count)
            "h" -> Duration.ofHours(count)
            "d" -> Duration.ofDays(count)
            "w" -> Duration.ofDays(count * 7)
            else -> null
        }
    }
    return null
}

/**
 * Formats a duration into a human readable string and truncates to the highest unit.
 */
fun formatDurationForHumans(duration: Duration): String {
    return DurationFormatUtils.formatDurationWords(
        truncateDuration(duration).toMillis(),
        true,
        true
    )
}

/**
 * Truncates a duration to the highest available unit.
 */
fun truncateDuration(duration: Duration): Duration {
    val days = duration.toDays()
    return if (days != 0L) {
        Duration.ofDays(days)
    } else {
        val hours = duration.toHours()
        if (hours != 0L) {
            Duration.ofHours(hours)
        } else {
            val minutes = duration.toMinutes()
            if (minutes != 0L) {
                Duration.ofMinutes(minutes)
            } else {
                Duration.ofSeconds(duration.seconds)
            }
        }
    }
}

/**
 * Replaces the Kotlin deprecated functions.
 */
@ExperimentalTime
val Int.seconds get() = toDuration(DurationUnit.SECONDS)
