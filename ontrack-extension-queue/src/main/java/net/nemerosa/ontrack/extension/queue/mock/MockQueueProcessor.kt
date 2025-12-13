package net.nemerosa.ontrack.extension.queue.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.queue.QueueMetadata
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Profile(RunProfile.DEV)
@Component
class MockQueueProcessor : QueueProcessor<MockQueuePayload> {

    override val id: String = "mock"

    override val payloadType: KClass<MockQueuePayload> = MockQueuePayload::class

    override fun isCancelled(payload: MockQueuePayload): String? =
            payload.message.takeIf {
                it.contains("cancelled", ignoreCase = true)
            }

    override fun process(payload: MockQueuePayload, queueMetadata: QueueMetadata?) {
        // Not doing anything (yet)
    }

    override fun getRoutingIdentifier(payload: MockQueuePayload): String = payload.message

}