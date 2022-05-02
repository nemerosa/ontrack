package net.nemerosa.ontrack.extension.casc.context.extensions

import net.nemerosa.ontrack.extension.casc.context.AbstractHolderContext
import org.springframework.stereotype.Component

@Component
class ExtensionsContext(
    subContexts: List<SubExtensionsContext>,
) : AbstractHolderContext<SubExtensionsContext>(
    subContexts,
    "List of configurations for the different extensions of Ontrack"
)