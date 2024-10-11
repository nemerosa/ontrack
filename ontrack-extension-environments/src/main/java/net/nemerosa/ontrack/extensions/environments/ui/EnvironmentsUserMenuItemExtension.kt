package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extensions.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extensions.environments.environmentFeatureEnabled
import net.nemerosa.ontrack.extensions.environments.security.EnvironmentList
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class EnvironmentsUserMenuItemExtension(
    private val securityService: SecurityService,
    environmentsExtensionFeature: EnvironmentsExtensionFeature,
    private val licenseControlService: LicenseControlService,
) : AbstractExtension(environmentsExtensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            UserMenuItem(
                groupId = CoreUserMenuGroups.INFORMATION,
                extension = feature,
                id = "environments",
                name = "Environments",
            ).takeIf {
                licenseControlService.environmentFeatureEnabled &&
                        securityService.isGlobalFunctionGranted(EnvironmentList::class.java)
            }
        )
}