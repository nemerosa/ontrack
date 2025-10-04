package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.ci.properties.PropertyAlias
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class ReleasePropertyAlias : PropertyAlias {
    override val alias: String = "release"
    override val type: String = ReleasePropertyType::class.java.name

    override fun parseConfig(data: JsonNode): JsonNode =
        ReleaseProperty(
            name = data.asText()
        ).asJson()
}