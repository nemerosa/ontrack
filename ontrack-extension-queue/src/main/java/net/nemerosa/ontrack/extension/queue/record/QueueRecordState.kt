package net.nemerosa.ontrack.extension.queue.record

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