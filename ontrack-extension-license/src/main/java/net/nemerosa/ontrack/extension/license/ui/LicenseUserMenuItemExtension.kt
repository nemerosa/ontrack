package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.license.LicenseExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class LicenseUserMenuItemExtension(
    licenseExtensionFeature: LicenseExtensionFeature,
) : AbstractExtension(licenseExtensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        get() = listOf(
            UserMenuItem(
                groupId = CoreUserMenuGroups.SYSTEM,
                extension = feature,
                id = "info",
                name = "License",
            )
        )

}