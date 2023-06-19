package net.nemerosa.ontrack.extension.queue.dispatching

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.queue.source.QueueSource

/**
 * Processing of payloads _before_ we send them to the queues.
 *
 * In particular, the dispatcher is responsible to determine the
 * routing information.
 */
interface QueueDispatcher {

    fun <T : Any> dispatch(
        queueProcessor: QueueProcessor<T>,
        payload: T,
        source: QueueSource?,
    ): QueueDispatchResult

}