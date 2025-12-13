package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class GitLabConfigurationsUserMenuItemExtension(
    private val gitLabExtensionFeature: GitLabExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(gitLabExtensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        get() = listOfNotNull(
            if (securityService.isGlobalFunctionGranted<GlobalSettings>()) {
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = "extension/${gitLabExtensionFeature.id}",
                    id = "configurations",
                    name = "GitLab configurations",
                )
            } else {
                null
            }
        )
}