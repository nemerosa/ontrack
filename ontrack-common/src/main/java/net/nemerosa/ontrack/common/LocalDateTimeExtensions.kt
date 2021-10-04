package net.nemerosa.ontrack.common

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * Converts a LDT into a Java date, using UTC time zone.
 *
 * Careful: the [LocalDateTime] should have been created by the methods in [Time].
 */
fun LocalDateTime.toJavaDate(): Date =
    Date.from(toInstant(ZoneOffset.UTC))
