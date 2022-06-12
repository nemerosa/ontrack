package net.nemerosa.ontrack.kdsl.spec.admin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class LogEntryDetail(
    val name: String,
    val description: String?,
)
