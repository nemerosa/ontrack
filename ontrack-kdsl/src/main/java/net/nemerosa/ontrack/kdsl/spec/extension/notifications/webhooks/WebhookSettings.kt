package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookSettings(
    val enabled: Boolean,
)
