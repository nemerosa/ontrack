package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("General settings for the webhooks")
data class WebhookSettings(
    @APIDescription("Are webhooks enabled?")
    val enabled: Boolean = false,
)
