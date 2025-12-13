package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.queue.source.QueueSourceExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class TestQueueSourceExtension(
        extension: TestExtensionFeature,
) : AbstractExtension(extension), QueueSourceExtension<String> {

    override val id: String = "test"

    companion object {
        val instance = TestQueueSourceExtension(TestExtensionFeature())
    }

}