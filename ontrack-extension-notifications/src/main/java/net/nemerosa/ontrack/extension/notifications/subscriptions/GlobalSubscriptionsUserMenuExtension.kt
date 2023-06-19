package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class GlobalSubscriptionsUserMenuExtension(
    extensionFeature: NotificationsExtensionFeature,
    private val notificationsConfigProperties: NotificationsConfigProperties,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction> = GlobalSubscriptionsManage::class.java

    override val action
        get() = Action(
            id = "global-subscriptions",
            name = "Global subscriptions",
            type = ActionType.LINK,
            uri = "global-subscriptions",
            group = UserMenuExtensionGroups.system,
            enabled = notificationsConfigProperties.enabled,
        )
}