package net.nemerosa.ontrack.extension.casc.schema.json

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.OntrackContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.model.json.schema.JsonSchema
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service

@Service
class CascJsonSchemaServiceImpl(
    private val envService: EnvService,
    private val ontrackContext: OntrackContext,
) : CascJsonSchemaService {

    override fun createCascJsonSchema(): JsonNode {
        val schema = JsonSchema(
            ref = "casc",
            id = "https://ontrack.run/${envService.version.display}/schema/casc",
            defs = emptyMap(),
            title = "Ontrack CasC",
            description = "Configuration as code for Ontrack",
            root = JsonObjectType(
                title = "Root Ontrack CasC",
                description = "Ontrack configuration as code",
                properties = mapOf(
                    "ontrack" to ontrackContext.jsonType,
                ),
                required = listOf("ontrack"),
                additionalProperties = false,
            )
        )
        return schema.asJson()
    }

}