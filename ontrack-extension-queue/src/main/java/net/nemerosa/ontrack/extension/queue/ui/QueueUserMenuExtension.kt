package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class QueueUserMenuExtension(
    extension: QueueExtensionFeature,
) : AbstractExtension(extension), UserMenuExtension {

    override fun getGlobalFunction(): Class<out GlobalFunction>? = null

    override fun getAction(): Action =
        Action.of("queue-records", "Queuing records", "records")

}