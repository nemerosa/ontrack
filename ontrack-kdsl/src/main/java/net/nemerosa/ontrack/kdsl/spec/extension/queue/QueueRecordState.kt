package net.nemerosa.ontrack.kdsl.spec.extension.queue

enum class QueueRecordState {

    STARTED,
    ROUTING_READY,
    SENT,
    RECEIVED,
    PARSED,
    PROCESSING,
    COMPLETED,
    ERRORED

}