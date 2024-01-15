package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

/**
 * Menus for the Jenkins: management of configurations.
 */
@Component
class JenkinsUserMenuItemExtension(
    private val jenkinsExtensionFeature: JenkinsExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(jenkinsExtensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/${jenkinsExtensionFeature.id}",
                    id = "configurations",
                    name = "Jenkins configurations",
                )
            } else {
                null
            }
        )

}