package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyLabel
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ServiceConfigurationSource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/extension/notifications/webhook")
class WebhookController(
    private val webhookAdminService: WebhookAdminService,
    private val webhookExecutionService: WebhookExecutionService,
    private val webhookAuthenticatorRegistry: WebhookAuthenticatorRegistry,
) {

    @GetMapping("create")
    fun newWebhookForm(): Form = webhookForm(null)

    @PostMapping("{name}/ping")
    fun pingWebhook(@PathVariable name: String) {
        val webhook = webhookAdminService.findWebhookByName(name)
            ?: throw WebhookNotFoundException(name)
        val payload = WebhookPingPayloadData.pingPayload("Webhook $name ping")
        webhookExecutionService.send(webhook, payload)
    }

    private fun webhookForm(webhook: Webhook?): Form = Form.create()
        .textField(WebhookForm::name, webhook?.name)
        .yesNoField(WebhookForm::enabled, webhook?.enabled ?: true)
        .urlField(WebhookForm::url, webhook?.url)
        .intField(WebhookForm::timeoutSeconds, webhook?.timeout?.toSeconds()?.toInt() ?: 60)
        .with(
            ServiceConfigurator.of(WebhookForm::authentication.name)
                .label(getPropertyLabel(WebhookForm::authentication))
                .help(getPropertyDescription(WebhookForm::authentication))
                .sources(
                    webhookAuthenticatorRegistry.authenticators.map { authenticator ->
                        ServiceConfigurationSource(
                            authenticator.type,
                            authenticator.displayName,
                            authenticator.getForm(null)
                        )
                    }
                )
                .value(
                    webhook?.authentication?.run {
                        ServiceConfiguration(type, config)
                    }
                )
        )


    data class WebhookForm(
        @APIDescription("Webhook unique name")
        @APILabel("Name")
        val name: String,
        @APIDescription("Webhook enabled or not")
        @APILabel("Enabled")
        val enabled: Boolean,
        @APIDescription("Webhook endpoint")
        @APILabel("URL")
        val url: String,
        @APIDescription("Webhook execution timeout (in seconds)")
        @APILabel("Timeout")
        val timeoutSeconds: Int,
        @APIDescription("Webhook authentication")
        @APILabel("Authentication")
        val authentication: WebhookAuthentication,
    )

}