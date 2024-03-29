package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class WebhooksUserMenuExtension(
    extensionFeature: NotificationsExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction> = WebhookManagement::class.java

    override val action = Action(
        id = "webhooks",
        name = "Webhooks",
        type = ActionType.LINK,
        uri = "webhooks",
        group = UserMenuExtensionGroups.system,
    )
}