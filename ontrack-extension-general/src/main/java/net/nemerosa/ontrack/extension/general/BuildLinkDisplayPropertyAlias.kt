package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.ci.properties.PropertyAlias
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class BuildLinkDisplayPropertyAlias : PropertyAlias {
    override val alias: String = "useLabel"
    override val type: String = BuildLinkDisplayPropertyType::class.java.name
    override fun parseConfig(data: JsonNode): JsonNode =
        BuildLinkDisplayProperty(
            useLabel = data.asBoolean()
        ).asJson()
}