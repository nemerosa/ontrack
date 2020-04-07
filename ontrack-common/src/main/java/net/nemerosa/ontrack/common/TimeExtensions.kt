package net.nemerosa.ontrack.common

import java.time.LocalDateTime

/**
 * Truncates a time down to 4 digits for the nanoseconds.
 *
 * @see [Time.truncate]
 */
fun LocalDateTime.truncate() = Time.truncate(this)
