package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class AutoVersioningQueueSourceExtension(
    autoVersioningExtensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(autoVersioningExtensionFeature), QueueSourceExtension<AutoVersioningQueueSourceData> {

    override val id: String = "auto-versioning"

}
