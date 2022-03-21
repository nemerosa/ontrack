package net.nemerosa.ontrack.extension.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.schema.CascType

interface CascContext {

    val type: CascType

    fun run(node: JsonNode, paths: List<String>)

    /**
     * Renders this context as JSON in order to generate the current settings.
     */
    fun render(): JsonNode

    /**
     * Priority of this context compared to all its siblings.
     *
     * Higher numbers have the higher priority.
     */
    val priority: Int get() = 0

}