package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.extension.casc.schema.CascNested
import net.nemerosa.ontrack.extension.casc.schema.CascPropertyType
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
    @JsonProperty("timeout-seconds")
    @CascPropertyType("int")
    val timeout: Duration,
    @APIDescription("Webhook authentication")
    @CascNested
    val authentication: WebhookAuthentication,
)