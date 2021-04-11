package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.casc.CascContext

interface SubCascContext: CascContext {

    val field: String

}

abstract class NOPSubCascContext: SubCascContext {

    override fun run(node: JsonNode, paths: List<String>) {}

    override val field: String = ""
}