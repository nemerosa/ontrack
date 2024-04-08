package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.model.support.CoreUserMenuGroups
import net.nemerosa.ontrack.model.support.UserMenuItem
import org.springframework.stereotype.Component

@Component
class GlobalSubscriptionsUserMenuItemExtension(
    private val notificationsExtensionFeature: NotificationsExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(notificationsExtensionFeature), UserMenuItemExtension {

    override val items: List<UserMenuItem>
        get() = if (securityService.isGlobalFunctionGranted<GlobalSubscriptionsManage>()) {
            listOf(
                UserMenuItem(
                    groupId = CoreUserMenuGroups.CONFIGURATIONS,
                    extension = notificationsExtensionFeature,
                    id = "subscriptions/global",
                    name = "Global subscriptions",
                )
            )
        } else {
            emptyList()
        }

}