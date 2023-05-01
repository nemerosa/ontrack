package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload

enum class QueueRecordState {

    STARTED,
    ROUTING_READY,
    SENT,
    RECEIVED,
    PARSED,
    CANCELLED,
    PROCESSING,
    COMPLETED,
    ERRORED

}