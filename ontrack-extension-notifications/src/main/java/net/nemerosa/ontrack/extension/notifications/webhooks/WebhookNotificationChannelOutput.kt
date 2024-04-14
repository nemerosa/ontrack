package net.nemerosa.ontrack.extension.notifications.webhooks

data class WebhookNotificationChannelOutput(
    val payload: WebhookPayload<*>,
)
