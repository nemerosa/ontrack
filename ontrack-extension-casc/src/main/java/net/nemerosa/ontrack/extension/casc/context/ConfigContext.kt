package net.nemerosa.ontrack.extension.casc.context

import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.model.json.schema.JsonType
import org.springframework.stereotype.Component

@Component
class ConfigContext(
    subContexts: List<SubConfigContext>,
) : AbstractHolderContext<SubConfigContext>(
    subContexts,
    "List of configurations"
) {

    companion object {
        const val PRIORITY: Int = 10
    }

    override val priority: Int = PRIORITY

    override val jsonType: JsonType by lazy {
        JsonObjectType(
            title = "Configurations",
            description = "List of configurations",
            properties = subContexts.associate {
                it.field to it.jsonType
            },
            required = emptyList(),
            additionalProperties = false,
        )
    }
}

interface SubConfigContext : SubCascContext
