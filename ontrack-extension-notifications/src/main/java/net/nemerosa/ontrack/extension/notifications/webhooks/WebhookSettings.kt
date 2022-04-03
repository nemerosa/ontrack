package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("General settings for the webhooks")
data class WebhookSettings(
    @APIDescription("Are webhooks enabled?")
    val enabled: Boolean = false,
    @APIDescription("Global timeout (in minutes) for all webhooks")
    val timeoutMinutes: Int = 5,
)
