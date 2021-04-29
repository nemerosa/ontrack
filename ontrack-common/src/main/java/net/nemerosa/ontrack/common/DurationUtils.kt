package net.nemerosa.ontrack.common

import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Formats a duration into a human readable string and truncates to the highest unit.
 */
fun formatDuration(duration: Duration): String {
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
