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

    /**
     * Puts a message (payload) onto a queue.
     *
     * @param queueProcessor Processor used for the processing of messages
     * @param payload Message to post
     * @param source Information about the source of the message
     */
    fun <T : Any> dispatch(
        queueProcessor: QueueProcessor<T>,
        payload: T,
        source: QueueSource?,
    ): QueueDispatchResult

}