package net.nemerosa.ontrack.extension.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.core.admin.AdminContext
import net.nemerosa.ontrack.extension.casc.context.extensions.ExtensionsContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.model.json.schema.JsonType
import org.springframework.stereotype.Component

@Component
class OntrackContext(
    private val configContext: ConfigContext,
    private val adminContext: AdminContext,
    private val extensionsContext: ExtensionsContext,
) : AbstractCascContext() {

    override fun run(node: JsonNode, paths: List<String>) {
        node.run(
            paths,
            "config" to configContext,
            "admin" to adminContext,
            "extensions" to extensionsContext,
        )
    }

    override val jsonType: JsonType by lazy {
        JsonObjectType(
            title = "Ontrack Casc",
            description = "Ontrack Casc root object",
            properties = mapOf(
                "config" to configContext.jsonType,
                "admin" to adminContext.jsonType,
                "extensions" to extensionsContext.jsonType,
            ),
            required = emptyList(),
            additionalProperties = false,
        )
    }

    override fun render(): JsonNode = mapOf(
        "config" to configContext.render(),
        "admin" to adminContext.render(),
        "extensions" to extensionsContext.render(),
    ).asJson()
}