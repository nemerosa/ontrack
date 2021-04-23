package net.nemerosa.ontrack.extension.casc.context

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.extension.casc.schema.with
import net.nemerosa.ontrack.json.asJson
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

    override fun render(): JsonNode = mapOf(
        "config" to configContext.render()
    ).asJson()
}