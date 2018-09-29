package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode

class ValidationRunDataRequest(
        id: String,
        val type: String? = null,
        data: JsonNode? = null
) : ServiceConfiguration(id, data)
