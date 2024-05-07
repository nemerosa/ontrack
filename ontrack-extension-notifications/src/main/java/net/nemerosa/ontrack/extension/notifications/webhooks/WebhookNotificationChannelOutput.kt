package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationField

data class WebhookNotificationChannelOutput(
    @APIDescription("Description of the payload sent to the webhook")
    @DocumentationField
    val payload: WebhookPayload<*>,
)
