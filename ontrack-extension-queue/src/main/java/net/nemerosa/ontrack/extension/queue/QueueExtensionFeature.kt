package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class QueueExtensionFeature : AbstractExtensionFeature(
    "queue",
    "Queuing",
    "Framework for the other extensions to deal with asynchronous and monitored processing.",
    ExtensionFeatureOptions.DEFAULT
)
