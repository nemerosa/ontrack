package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.extension.queue.source.QueueSource

/**
 * Recording events on queue items.
 */
interface QueueRecordService {

    fun start(queuePayload: QueuePayload, source: QueueSource?)
    fun setRouting(queuePayload: QueuePayload, routingKey: String)
    fun sent(queuePayload: QueuePayload)

    fun received(queuePayload: QueuePayload, queue: String?)
    fun parsed(queuePayload: QueuePayload, payload: Any)
    fun cancelled(queuePayload: QueuePayload, cancelReason: String)
    fun processing(queuePayload: QueuePayload)
    fun completed(queuePayload: QueuePayload)
    fun errored(queuePayload: QueuePayload, exception: Exception)

}