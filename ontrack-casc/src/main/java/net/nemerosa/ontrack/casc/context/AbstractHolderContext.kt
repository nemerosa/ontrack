package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.casc.schema.cascObject
import net.nemerosa.ontrack.casc.schema.with
import org.springframework.stereotype.Component

abstract class AbstractHolderContext<T : SubCascContext>(
    private val subContexts: List<T>,
    private val description: String,
) : AbstractCascContext() {

    override val type
        get() = cascObject(
            description = description,
            *subContexts.map {
                it.field to it.with(it.type.description)
            }.toTypedArray()
        )

    override fun run(node: JsonNode, paths: List<String>) {
        node.runSubCascContexts(paths, subContexts)
    }
}
