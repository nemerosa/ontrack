package net.nemerosa.ontrack.extension.config.ci.engine

import org.springframework.stereotype.Component

@Component
class GenericCIEngine : CIEngine {
    override val name: String = "generic"

    /**
     * Never matching, it must be declared explicitly.
     */
    override fun matchesEnv(env: Map<String, String>): Boolean = false
}