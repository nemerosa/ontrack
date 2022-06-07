package net.nemerosa.ontrack.kdsl.spec.support

import com.fasterxml.jackson.databind.JsonNode

data class ServiceConfiguration(
    val id: String,
    val data: JsonNode?,
)