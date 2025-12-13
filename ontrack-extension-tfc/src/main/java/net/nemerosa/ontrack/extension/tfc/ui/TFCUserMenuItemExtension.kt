package net.nemerosa.ontrack.extension.tfc.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.tfc.TFCExtensionFeature
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class TFCUserMenuItemExtension(
    tfcExtensionFeature: TFCExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(tfcExtensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/${feature.id}",
                    id = "configurations",
                    name = "TFC configurations",
                )
            } else {
                null
            }
        )
}