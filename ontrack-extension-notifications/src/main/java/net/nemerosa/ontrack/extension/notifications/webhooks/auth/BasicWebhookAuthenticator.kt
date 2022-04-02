package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component
import java.net.http.HttpRequest
import java.util.*

@Component
class BasicWebhookAuthenticator : AbstractWebhookAuthenticator<BasicWebhookAuthenticatorConfig>() {

    override val type: String = "basic"

    override fun validateConfig(node: JsonNode) = node.parse<BasicWebhookAuthenticatorConfig>()

    override fun authenticate(config: BasicWebhookAuthenticatorConfig, builder: HttpRequest.Builder) {
        val encoded = Base64.getEncoder().encodeToString(
            "${config.username}:${config.password}".toByteArray()
        )
        builder.header("Authorization", "Basic $encoded")
    }

}

data class BasicWebhookAuthenticatorConfig(
    val username: String,
    val password: String,
)
