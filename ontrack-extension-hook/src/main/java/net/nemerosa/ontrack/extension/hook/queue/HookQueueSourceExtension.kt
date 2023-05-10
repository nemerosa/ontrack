package net.nemerosa.ontrack.extension.hook.queue

import net.nemerosa.ontrack.extension.hook.HookExtensionFeature
import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class HookQueueSourceExtension(
        extensionFeature: HookExtensionFeature,
) : AbstractExtension(extensionFeature), QueueSourceExtension<HookQueueSourceData> {

    override val id: String = "hook"

}