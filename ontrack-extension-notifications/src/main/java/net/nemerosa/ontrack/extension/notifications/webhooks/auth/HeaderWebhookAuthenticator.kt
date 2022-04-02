package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component
import java.net.http.HttpRequest

@Component
class HeaderWebhookAuthenticator : AbstractWebhookAuthenticator<HeaderWebhookAuthenticatorConfig>() {

    override val type: String = "header"

    override fun validateConfig(node: JsonNode) = node.parse<HeaderWebhookAuthenticatorConfig>()

    override fun authenticate(config: HeaderWebhookAuthenticatorConfig, builder: HttpRequest.Builder) {
        builder.header(config.name, config.value)
    }

}

data class HeaderWebhookAuthenticatorConfig(
    val name: String,
    val value: String,
)
