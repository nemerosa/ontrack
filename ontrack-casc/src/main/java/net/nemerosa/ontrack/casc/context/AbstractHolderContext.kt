package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component

@Component
class AbstractHolderContext<T : SubCascContext>(
    private val subContexts: List<T>,
) : AbstractCascContext() {
    override fun run(node: JsonNode, paths: List<String>) {
        node.runSubCascContexts(paths, subContexts)
    }
}
