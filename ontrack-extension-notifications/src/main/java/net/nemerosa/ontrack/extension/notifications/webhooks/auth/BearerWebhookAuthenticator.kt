package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import org.springframework.stereotype.Component
import java.net.http.HttpRequest

@Component
class BearerWebhookAuthenticator : AbstractWebhookAuthenticator<BearerWebhookAuthenticatorConfig>() {

    override val type: String = "bearer"

    override val displayName: String = "Bearer token authentication"

    override fun validateConfig(node: JsonNode): BearerWebhookAuthenticatorConfig = node.parse()

    override fun authenticate(config: BearerWebhookAuthenticatorConfig, builder: HttpRequest.Builder) {
        builder.header("Authorization", "Bearer ${config.token}")
    }

}

data class BearerWebhookAuthenticatorConfig(
    @APILabel("Token")
    @APIDescription("Token used to connect to the webhook")
    val token: String,
)
