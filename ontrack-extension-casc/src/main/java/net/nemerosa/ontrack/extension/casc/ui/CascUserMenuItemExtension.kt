package net.nemerosa.ontrack.extension.casc.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class CascUserMenuItemExtension(
    extensionFeature: CascExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() {
            val list = mutableListOf<UserMenuItem>()

            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                list += UserMenuItem(
                    groupId = CoreUserMenuGroups.SYSTEM,
                    extension = feature,
                    id = "casc",
                    name = "Configuration as Code",
                )
            }

            return list.toList()
        }
}