package net.nemerosa.ontrack.extension.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.model.json.schema.JsonType

abstract class AbstractHolderContext<T : SubCascContext>(
    private val subContexts: List<T>,
    private val description: String,
) : AbstractCascContext() {

    override val jsonType: JsonType by lazy {
        JsonObjectType(
            title = this::class.java.simpleName,
            description = description,
            properties = subContexts.associate { ctx ->
                ctx.field to ctx.jsonType
            },
            required = emptyList(),
        )
    }

    override fun run(node: JsonNode, paths: List<String>) {
        node.runSubCascContexts(paths, subContexts)
    }

    override fun render(): JsonNode = subContexts.associate {
        it.field to it.render()
    }.asJson()
}
