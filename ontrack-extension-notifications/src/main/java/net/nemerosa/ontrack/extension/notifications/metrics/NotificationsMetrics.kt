package net.nemerosa.ontrack.extension.notifications.metrics

/**
 * Names of the metrics for the notifications.
 */
object NotificationsMetrics {

    /**
     * Prefix for all metric names
     */
    private const val prefix = "ontrack_extension_notifications"

    const val event_listening_received = "${prefix}_event_listening_received"
    const val event_listening_queued = "${prefix}_event_listening_queued"
    const val event_listening_dequeued = "${prefix}_event_listening_dequeued"
    const val event_listening_dequeued_error = "${prefix}_event_listening_dequeued_error"
    const val event_listening = "${prefix}_event_listening"

}