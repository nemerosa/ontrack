package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class AutoVersioningAuditUserMenuExtension(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    /**
     * All authenticated users are allowed
     */
    override fun getGlobalFunction(): Class<out GlobalFunction>? = null

    override fun getAction(): Action =
        Action.of("auto-versioning-audit-global", "Auto versioning audit", "audit/global")

}