package net.nemerosa.ontrack.extension.notifications.webhooks

import java.time.Duration

data class Webhook(
    val name: String,
    val enabled: Boolean,
    val url: String,
    val timeout: Duration,
)