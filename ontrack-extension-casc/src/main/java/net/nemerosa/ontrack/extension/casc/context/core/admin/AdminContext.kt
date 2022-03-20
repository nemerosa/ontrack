package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.context.AbstractHolderContext
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
)
