package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class GlobalSubscriptionsUserMenuExtension(
    extensionFeature: NotificationsExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override fun getGlobalFunction(): Class<out GlobalFunction> = GlobalSubscriptionsManage::class.java

    override fun getAction() = Action(
        id = "global-subscriptions",
        name = "Global subscriptions",
        type = ActionType.LINK,
        uri = "global-subscriptions",
    )
}