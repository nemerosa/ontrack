package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class AutoVersioningAuditUserMenuItemExtension(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = listOf(
            // All users can access the AV audit
            UserMenuItem(
                groupId = CoreUserMenuGroups.INFORMATION,
                extension = "extension/auto-versioning",
                id = "audit/global",
                name = "Auto-versioning audit",
            )
        )

}