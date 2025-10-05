package net.nemerosa.ontrack.extension.config.ci.conditions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import org.springframework.stereotype.Component

@Component
class BranchCondition : Condition {
    override val name: String = "branch"
    override fun matches(
        ciEngine: CIEngine,
        config: JsonNode,
        env: Map<String, String>
    ): Boolean {
        val ciBranchName = ciEngine.getBranchName(env)
        val regexBranchName = config.asText().toRegex()
        return regexBranchName.matches(ciBranchName ?: "")
    }
}