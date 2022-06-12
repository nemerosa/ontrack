package net.nemerosa.ontrack.kdsl.spec.admin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class LogEntry(
    val information: String,
    val detailList: List<LogEntryDetail>,
)