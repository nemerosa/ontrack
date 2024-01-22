package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

/**
 * Menus for the GitHub management of configurations.
 */
@Component
class GitHubUserMenuItemExtension(
    private val gitHubExtensionFeature: GitHubExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(gitHubExtensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/${gitHubExtensionFeature.id}",
                    id = "configurations",
                    name = "GitHub configurations",
                )
            } else {
                null
            }
        )

}