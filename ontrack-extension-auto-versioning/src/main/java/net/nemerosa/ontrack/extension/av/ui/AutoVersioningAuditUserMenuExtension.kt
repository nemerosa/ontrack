package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
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
    override val globalFunction: Class<out GlobalFunction>? = null

    override val action: Action =
        Action.of("auto-versioning-audit-global", "Auto versioning audit", "audit/global")
            .withGroup(UserMenuExtensionGroups.information)

}