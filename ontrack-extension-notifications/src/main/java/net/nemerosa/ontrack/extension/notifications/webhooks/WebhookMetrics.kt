package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics

/**
 * Names of the metrics for the webhooks.
 */
object WebhookMetrics {

    /**
     * Prefix for all metric names
     */
    private const val prefix = "ontrack_extension_notifications_webhooks"

    const val webhook_delivery_started = "${prefix}_delivery_started"
    const val webhook_delivery_answered = "${prefix}_delivery_answered"
    const val webhook_delivery_error = "${prefix}_delivery_error"
    const val webhook_delivery_duration = "${prefix}_delivery_duration"
}