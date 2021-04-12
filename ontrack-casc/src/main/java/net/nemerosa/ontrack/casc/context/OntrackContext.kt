package net.nemerosa.ontrack.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.casc.schema.CascType
import net.nemerosa.ontrack.casc.schema.cascObject
import net.nemerosa.ontrack.casc.schema.with
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

    override val type: CascType = cascObject(
        "Root for the configuration as code",
        "config" to configContext.with("List of configurations")
    )
}