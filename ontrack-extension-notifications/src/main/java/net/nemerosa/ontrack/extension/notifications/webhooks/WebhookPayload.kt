package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.API
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.docs.SelfDocumented
import java.util.*

/**
 * Minimal requirements for the payload of a webhook.
 */
@SelfDocumented
data class WebhookPayload<T>(
    @APIDescription("Unique ID for the payload")
    @DocumentationType("String")
    val uuid: UUID = UUID.randomUUID(),
    @APIDescription("Webhook type")
    val type: String,
    @APIDescription("Webhook actual payload")
    @DocumentationType("JSON")
    val data: T,
)
