package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class SonarQubeUserMenuItemExtension(
    private val extensionFeature: SonarQubeExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/${extensionFeature.id}",
                    id = "configurations",
                    name = "SonarQube configurations",
                )
            } else {
                null
            }
        )
}
