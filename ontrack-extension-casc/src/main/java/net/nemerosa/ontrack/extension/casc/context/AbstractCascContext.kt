package net.nemerosa.ontrack.extension.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.CascContext
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.parse

abstract class AbstractCascContext : net.nemerosa.ontrack.extension.casc.CascContext {

    /**
     * Known list of fields to handle
     */
    protected fun JsonNode.run(
        paths: List<String>,
        vararg mapping: Pair<String, net.nemerosa.ontrack.extension.casc.CascContext>,
    ) {
        run(paths, mapping.toMap())
    }

    /**
     * Sub contexts registred for fields
     */
    protected fun JsonNode.runSubCascContexts(
        paths: List<String>,
        subCascContexts: List<SubCascContext>,
    ) {
        run(paths, subCascContexts.associateBy { it.field })
    }

    /**
     * Checked parsing of a JSON array
     */
    protected inline fun <reified T> JsonNode.mapEachTo(paths: List<String>) =
        mapIndexed { index, child ->
            try {
                child.parse<T>()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${T::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

    /**
     * Known list of fields to handle
     */
    protected fun JsonNode.run(
        paths: List<String>,
        mapping: Map<String, net.nemerosa.ontrack.extension.casc.CascContext>,
    ) {
        fields().forEach { (name, value) ->
            val context = mapping[name]
            if (context != null) {
                context.run(value, paths + name)
            } else {
                error("No CasC context is defined for ${path(paths + name)}")
            }
        }
    }

    protected fun path(paths: List<String>) =
        paths.joinToString("/")

    protected class SubContext(
        val node: JsonNode,
        val paths: List<String>,
    ) {
        fun runWith(context: net.nemerosa.ontrack.extension.casc.CascContext) {
            context.run(node, paths)
        }
    }

    protected infix fun SubContext?.run(context: net.nemerosa.ontrack.extension.casc.CascContext) {
        this?.runWith(context)
    }
}