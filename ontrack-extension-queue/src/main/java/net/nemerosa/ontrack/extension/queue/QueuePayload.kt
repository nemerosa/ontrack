package net.nemerosa.ontrack.extension.queue

import java.util.*

class QueuePayload private constructor(
    val id: String,
    val processor: String,
    val body: Any,
) {
    companion object {
        fun <T : Any> create(processor: QueueProcessor<T>, body: T) =
            QueuePayload(
                id = UUID.randomUUID().toString(),
                processor = processor::class.java.name,
                body = body
            )
    }
}
