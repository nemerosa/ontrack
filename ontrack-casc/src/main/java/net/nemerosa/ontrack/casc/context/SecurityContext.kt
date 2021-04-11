package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component

@Component
class SecurityContext(
    private val subSecurityContexts: List<SubSecurityContext>,
) : AbstractCascContext() {

    override fun run(node: JsonNode, paths: List<String>) {
        node.runSubCascContexts(paths, subSecurityContexts)
    }
}