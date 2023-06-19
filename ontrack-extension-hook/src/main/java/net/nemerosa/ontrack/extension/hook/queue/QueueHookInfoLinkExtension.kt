package net.nemerosa.ontrack.extension.hook.queue

import net.nemerosa.ontrack.extension.hook.HookExtensionFeature
import net.nemerosa.ontrack.extension.hook.HookInfoLinkExtension
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatchResult
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class QueueHookInfoLinkExtension(
        extensionFeature: HookExtensionFeature,
) : AbstractExtension(extensionFeature), HookInfoLinkExtension<List<QueueDispatchResult>> {

    override val id: String = "queue"
}