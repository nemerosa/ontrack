package net.nemerosa.ontrack.extension.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.CascContext

interface SubCascContext: net.nemerosa.ontrack.extension.casc.CascContext {

    val field: String

}

abstract class NOPSubCascContext: SubCascContext {

    override fun run(node: JsonNode, paths: List<String>) {}

    override val field: String = ""
}