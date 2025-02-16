package net.nemerosa.ontrack.extension.casc.schema.json

import com.fasterxml.jackson.databind.JsonNode

interface CascJsonSchemaService {

    fun createCascJsonSchema(): JsonNode

}