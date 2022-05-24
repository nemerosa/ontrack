package net.nemerosa.ontrack.common

import java.time.Duration
import java.time.LocalDateTime

/**
 * Truncates a time down to 4 digits for the nanoseconds.
 *
 * @see [Time.truncate]
 */
fun LocalDateTime.truncate() = Time.truncate(this)


/**
 * Converts a number of hours into a [Duration].
 */
val Int.hours: Duration get() = Duration.ofHours(toLong())

/**
 * Converts a number of minutes into a [Duration].
 */
val Int.minutes: Duration get() = Duration.ofMinutes(toLong())
