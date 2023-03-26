package net.nemerosa.ontrack.extension.queue.dispatching

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Synchronous queue dispatching (no queue).
 *
 * Used for testing.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.queue",
    name = ["async"],
    havingValue = "false",
    matchIfMissing = false,
)
class SyncQueueDispatcher : QueueDispatcher {

    /**
     * Just launches the processing.
     */
    override fun <T : Any> dispatch(queueProcessor: QueueProcessor<T>, payload: T): String {
        queueProcessor.process(payload)
        return "n/a"
    }

}