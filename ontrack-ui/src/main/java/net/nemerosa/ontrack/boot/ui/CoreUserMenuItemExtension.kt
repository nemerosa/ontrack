package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.CoreExtensionFeature
import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class CoreUserMenuItemExtension(
    private val securityService: SecurityService,
) : AbstractExtension(CoreExtensionFeature.INSTANCE), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() {
            val globalSettings = securityService.isGlobalFunctionGranted<GlobalSettings>()

            val items = mutableListOf<UserMenuItem>()

            items += UserMenuItem(
                groupId = CoreUserMenuGroups.USER,
                extension = "core",
                id = "UserProfile",
                name = "User profile",
            )

            if (globalSettings) {
                items += UserMenuItem(
                    groupId = CoreUserMenuGroups.SYSTEM,
                    extension = "core",
                    id = "Settings",
                    name = "Settings",
                )
            }

            return items
        }
}