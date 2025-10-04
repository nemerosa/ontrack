package net.nemerosa.ontrack.extension.git.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.ci.properties.PropertyAlias
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class GitCommitPropertyAlias : PropertyAlias {
    override val alias: String = "gitCommit"
    override val type: String = GitCommitPropertyType::class.java.name

    override fun parseConfig(data: JsonNode): JsonNode =
        GitCommitProperty(
            commit = data.asText()
        ).asJson()
}