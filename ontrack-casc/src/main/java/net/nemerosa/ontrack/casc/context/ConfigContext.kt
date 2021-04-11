package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component

@Component
class ConfigContext(
    private val securityContext: SecurityContext,
) : AbstractCascContext() {

    override fun run(node: JsonNode, paths: List<String>) {
        node.run(paths,
            "security" to securityContext
        )
    }
}