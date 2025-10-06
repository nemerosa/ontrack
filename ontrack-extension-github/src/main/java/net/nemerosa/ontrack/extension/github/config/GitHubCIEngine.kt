package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import org.springframework.stereotype.Component

@Component
class GitHubCIEngine: CIEngine {

    override val name: String = "github"

    override fun matchesEnv(env: Map<String, String>): Boolean {
        TODO("Not yet implemented")
    }
}