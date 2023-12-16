package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.CoreExtensionFeature
import net.nemerosa.ontrack.extension.api.UserMenuGroupExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuGroup
import org.springframework.stereotype.Component

@Component
class CoreUserMenuGroupExtension : AbstractExtension(CoreExtensionFeature.INSTANCE), UserMenuGroupExtension {
    override val groups: List<UserMenuGroup> = listOf(
        UserMenuGroup(CoreUserMenuGroups.USER, "User information"),
        UserMenuGroup(CoreUserMenuGroups.SYSTEM, "System"),
    )
}
