package net.nemerosa.ontrack.extension.config.ci.engine

import org.springframework.stereotype.Component

@Component
class GenericCIEngine: CIEngine {
    override val name: String = "generic"
}