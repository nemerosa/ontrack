package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import org.springframework.stereotype.Component
import java.net.http.HttpRequest

@Component
class HeaderWebhookAuthenticator : AbstractWebhookAuthenticator<HeaderWebhookAuthenticatorConfig>() {

    override val type: String = "header"

    override val displayName: String = "HTTP Header authentication"

    override fun validateConfig(node: JsonNode): HeaderWebhookAuthenticatorConfig = node.parse()

    override fun authenticate(config: HeaderWebhookAuthenticatorConfig, builder: HttpRequest.Builder) {
        builder.header(config.name, config.value)
    }

}

data class HeaderWebhookAuthenticatorConfig(
    @APILabel("Header name")
    @APIDescription("Name of the header to send to the webhook")
    val name: String,
    @APILabel("Header value")
    @APIDescription("Value of the header to send to the webhook")
    val value: String,
)
