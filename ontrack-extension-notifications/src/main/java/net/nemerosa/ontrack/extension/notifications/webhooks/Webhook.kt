package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.Duration

@APIDescription("Webhook registration")
data class Webhook(
    @APIDescription("Webhook unique name")
    val name: String,
    @APIDescription("Webhook enabled or not")
    val enabled: Boolean,
    @APIDescription("Webhook endpoint")
    val url: String,
    @APIDescription("Webhook execution timeout (in seconds)")
    val timeout: Duration,
    @APIDescription("Webhook authentication")
    val authentication: WebhookAuthentication,
)