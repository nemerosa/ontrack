package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

@APIDescription("General settings for the webhooks")
data class WebhookSettings(
    @APIDescription("Are webhooks enabled?")
    val enabled: Boolean = false,
    @APIDescription("Global timeout (in minutes) for all webhooks")
    val timeoutMinutes: Int = 5,
    @APIDescription("Retention time (in days) for the archiving of webhook deliveries")
    @APILabel("Delivery retention days")
    val deliveriesRetentionDays: Int = 30,
)
