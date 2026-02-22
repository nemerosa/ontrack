package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class ScmSearchIndexQueueSourceExtension(
    feature: SCMExtensionFeature
) : AbstractExtension(feature), QueueSourceExtension<ScmSearchIndexQueueSourceData> {
    override val id: String = "scm-search-index"
}