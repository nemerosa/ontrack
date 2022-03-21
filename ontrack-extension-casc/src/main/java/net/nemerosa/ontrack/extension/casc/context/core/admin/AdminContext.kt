package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.context.AbstractHolderContext
import net.nemerosa.ontrack.extension.casc.context.ConfigContext
import org.springframework.stereotype.Component

/**
 * CasC context for the administration resources of Ontrack.
 */
@Component
class AdminContext(
    subContexts: List<SubAdminContext>,
) : AbstractHolderContext<SubAdminContext>(
    subContexts,
    "Administration resources of Ontrack."
) {
    /**
     * Admin context must be processed _after_ the config one.
     */
    override val priority: Int = ConfigContext.PRIORITY - 5
}
