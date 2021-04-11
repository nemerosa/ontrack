package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component

@Component
class OntrackContext(
    private val configContext: ConfigContext,
) : AbstractCascContext() {

    override fun run(node: JsonNode, paths: List<String>) {
        node.run(
            paths,
            "config" to configContext,
        )
    }
}