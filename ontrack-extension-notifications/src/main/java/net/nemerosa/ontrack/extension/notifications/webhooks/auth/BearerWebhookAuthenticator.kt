package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component
import java.net.http.HttpRequest

@Component
class BearerWebhookAuthenticator : AbstractWebhookAuthenticator<BearerWebhookAuthenticatorConfig>() {

    override val type: String = "bearer"

    override fun validateConfig(node: JsonNode) = node.parse<BearerWebhookAuthenticatorConfig>()

    override fun authenticate(config: BearerWebhookAuthenticatorConfig, builder: HttpRequest.Builder) {
        builder.header("Authorization", "Bearer ${config.token}")
    }

}

data class BearerWebhookAuthenticatorConfig(
    val token: String,
)
