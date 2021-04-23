package net.nemerosa.ontrack.extension.casc.context

import org.springframework.stereotype.Component

@Component
class ConfigContext(
    subContexts: List<SubConfigContext>,
) : AbstractHolderContext<SubConfigContext>(
    subContexts,
    "List of configurations"
)

interface SubConfigContext : SubCascContext
