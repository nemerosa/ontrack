package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class QueueUserMenuExtension(
    extension: QueueExtensionFeature,
) : AbstractExtension(extension), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction>? = null

    override val action: Action =
        Action.of("queue-records", "Queuing records", "records")
                .withGroup(UserMenuExtensionGroups.information)

}