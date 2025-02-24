package net.nemerosa.ontrack.extension.casc.context

import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.springframework.stereotype.Component

@Component
class ConfigContext(
    private val subContexts: List<SubConfigContext>,
) : AbstractHolderContext<SubConfigContext>(
    subContexts,
    "List of configurations"
) {

    companion object {
        const val PRIORITY: Int = 10
    }

    override val priority: Int = PRIORITY

    override fun jsonType(jsonTypeBuilder: JsonTypeBuilder): JsonType {
        return JsonObjectType(
            title = "Configurations",
            description = "List of configurations",
            properties = subContexts.associate {
                it.field to it.jsonType(jsonTypeBuilder)
            },
            required = emptyList(),
            additionalProperties = false,
        )
    }
}

interface SubConfigContext : SubCascContext
