package net.nemerosa.ontrack.extension.queue.record

interface QueueRecordQueryService {

    fun findByQueuePayloadID(id: String): QueueRecord?

}