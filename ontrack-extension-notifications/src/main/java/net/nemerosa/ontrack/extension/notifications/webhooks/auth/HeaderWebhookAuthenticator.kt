package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.passwordField
import net.nemerosa.ontrack.model.form.textField
import org.springframework.stereotype.Component
import java.net.http.HttpRequest

@Component
class HeaderWebhookAuthenticator : AbstractWebhookAuthenticator<HeaderWebhookAuthenticatorConfig>() {

    override val type: String = "header"

    override val displayName: String = "HTTP Header authentication"

    override fun getForm(config: HeaderWebhookAuthenticatorConfig?): Form = Form.create()
        .textField(HeaderWebhookAuthenticatorConfig::name, config?.name)
        .passwordField(HeaderWebhookAuthenticatorConfig::value)

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
