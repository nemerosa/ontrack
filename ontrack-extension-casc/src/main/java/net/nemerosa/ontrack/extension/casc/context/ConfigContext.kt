package net.nemerosa.ontrack.extension.casc.context

import org.springframework.stereotype.Component

@Component
class ConfigContext(
    subContexts: List<SubConfigContext>,
) : AbstractHolderContext<SubConfigContext>(
    subContexts,
    "List of configurations"
) {

    companion object {
        const val PRIORITY: Int = 10
    }

    override val priority: Int = PRIORITY

}

interface SubConfigContext : SubCascContext
