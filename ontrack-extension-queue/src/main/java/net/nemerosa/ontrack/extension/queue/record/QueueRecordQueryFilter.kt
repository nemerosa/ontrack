package net.nemerosa.ontrack.extension.queue.record

data class QueueRecordQueryFilter(
    val id: String? = null,
    val processor: String? = null,
    val state: QueueRecordState? = null,
    val routingKey: String? = null,
    val queueName: String? = null,
    val text: String? = null,
)
