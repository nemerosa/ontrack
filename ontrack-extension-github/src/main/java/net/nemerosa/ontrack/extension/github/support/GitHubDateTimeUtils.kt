package net.nemerosa.ontrack.extension.github.support

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun parseLocalDateTime(value: String) = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)
