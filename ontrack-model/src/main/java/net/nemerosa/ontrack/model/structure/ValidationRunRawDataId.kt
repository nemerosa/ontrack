package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode

class ValidationRunRawDataId(
        val id: String,
        val data: JsonNode
)