package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.extension.environments.security.EnvironmentList
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class EnvironmentsUserMenuItemExtension(
    private val securityService: SecurityService,
    environmentsExtensionFeature: EnvironmentsExtensionFeature,
    private val environmentsLicense: EnvironmentsLicense,
) : AbstractExtension(environmentsExtensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            UserMenuItem(
                groupId = CoreUserMenuGroups.INFORMATION,
                extension = feature,
                id = "environments",
                name = "Environments",
            ).takeIf {
                environmentsLicense.environmentFeatureEnabled &&
                        securityService.isGlobalFunctionGranted(EnvironmentList::class.java)
            }
        )
}