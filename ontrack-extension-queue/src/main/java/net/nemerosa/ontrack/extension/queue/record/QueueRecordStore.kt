package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload

interface QueueRecordStore {
    fun start(queuePayload: QueuePayload)

    fun save(queuePayload: QueuePayload, code: (QueueRecord) -> QueueRecord)
}