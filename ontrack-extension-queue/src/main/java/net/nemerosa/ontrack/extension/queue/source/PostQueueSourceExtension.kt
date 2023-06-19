package net.nemerosa.ontrack.extension.queue.source

import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class PostQueueSourceExtension(
        extensionFeature: QueueExtensionFeature,
) : AbstractExtension(extensionFeature), QueueSourceExtension<String> {

    override val id: String = "post"

}