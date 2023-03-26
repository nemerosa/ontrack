package net.nemerosa.ontrack.extension.queue

class QueuePayload private constructor(
    val processor: String,
    val body: Any,
) {
    companion object {
        fun <T : Any> create(processor: QueueProcessor<T>, body: T) =
            QueuePayload(processor::class.java.name, body)
    }
}
