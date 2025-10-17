package net.nemerosa.ontrack.extension.config.ci.engine

import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class GenericCIEngine : CIEngine {
    override val name: String = "generic"

    /**
     * Never matching, it must be declared explicitly.
     */
    override fun matchesEnv(env: Map<String, String>): Boolean = false

    /**
     * Doing nothing.
     */
    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>
    ) {
    }
}