package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extensions.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class EnvironmentsUserMenuItemExtension(
    private val environmentsExtensionFeature: EnvironmentsExtensionFeature,
) : AbstractExtension(environmentsExtensionFeature), UserMenuItemExtension {
    override val items: List<UserMenuItem>
        // TODO Security check
        get() = listOf(
            UserMenuItem(
                groupId = CoreUserMenuGroups.INFORMATION,
                extension = environmentsExtensionFeature,
                id = "environments",
                name = "Environments",
            )
        )
}