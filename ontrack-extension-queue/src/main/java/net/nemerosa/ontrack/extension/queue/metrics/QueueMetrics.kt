package net.nemerosa.ontrack.extension.queue.metrics

object QueueMetrics {

    const val messageSent = "ontrack_extension_queue_message_sent"
    const val messageReceived = "ontrack_extension_queue_message_received"

    const val processCompleted = "ontrack_extension_queue_process_completed"
    const val processErrored = "ontrack_extension_queue_process_errored"
    const val processTime = "ontrack_extension_queue_process_time"

    /**
     * Pending items into the queue
     */
    const val pending = "ontrack_extension_queue_pending"

}