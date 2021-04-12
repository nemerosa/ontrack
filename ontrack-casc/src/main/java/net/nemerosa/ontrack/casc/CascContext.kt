package net.nemerosa.ontrack.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.casc.schema.CascType

interface CascContext {

    val type: CascType

    fun run(node: JsonNode, paths: List<String>)

}