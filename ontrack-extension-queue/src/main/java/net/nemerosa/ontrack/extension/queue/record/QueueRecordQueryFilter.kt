package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.model.annotations.APIDescription

data class QueueRecordQueryFilter(
        @APIDescription("Unique ID for the record")
        val id: String? = null,
        @APIDescription("Filtering on the processor")
        val processor: String? = null,
        @APIDescription("Filtering on the state")
        val state: QueueRecordState? = null,
        @APIDescription("Filtering on the routing key")
        val routingKey: String? = null,
        @APIDescription("Filtering on the queue name")
        val queueName: String? = null,
        @APIDescription("Filtering on the username")
        val username: String? = null,
        @APIDescription("Filtering using some free text in the payload")
        val text: String? = null,
)
