package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

/**
 * Menus for the JIRA management of configurations.
 */
@Component
class JIRAUserMenuItemExtension(
    private val jiraExtensionFeature: JIRAExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(jiraExtensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/${jiraExtensionFeature.id}",
                    id = "configurations",
                    name = "Jira configurations",
                )
            } else {
                null
            }
        )

}