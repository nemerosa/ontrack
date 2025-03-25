package net.nemerosa.ontrack.extension.casc.schema.json

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.OntrackContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.*
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service

@Service
class CascJsonSchemaServiceImpl(
    private val envService: EnvService,
    private val jsonSchemaBuilderService: JsonSchemaBuilderService,
    private val ontrackContext: OntrackContext,
) : CascJsonSchemaService {

    override fun createCascJsonSchema(): JsonNode {
        return jsonSchemaBuilderService.createSchema(
            ref = "casc",
            id = "https://ontrack.run/${envService.version.display}/schema/casc",
            title = "Ontrack CasC",
            description = "Configuration as code for Ontrack",
            root = object : JsonTypeProvider {
                override fun jsonType(jsonTypeBuilder: JsonTypeBuilder): JsonType {
                    return JsonObjectType(
                        title = "Root Ontrack CasC",
                        description = "Ontrack configuration as code",
                        properties = mapOf(
                            "ontrack" to ontrackContext.jsonType(jsonTypeBuilder),
                        ),
                        required = listOf("ontrack"),
                        additionalProperties = false,
                    )
                }
            }
        ).asJson()
    }

}