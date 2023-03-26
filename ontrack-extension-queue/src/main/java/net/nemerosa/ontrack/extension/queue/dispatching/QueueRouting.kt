package net.nemerosa.ontrack.extension.queue.dispatching

import net.nemerosa.ontrack.extension.queue.QueueConfigProperties
import net.nemerosa.ontrack.extension.queue.QueueProcessor

object QueueRouting {

    fun <T: Any> getRoutingKey(
        queueConfigProperties: QueueConfigProperties,
        queueProcessor: QueueProcessor<T>,
        payload: T
    ): String {
        TODO("Not yet implemented")
    }

}