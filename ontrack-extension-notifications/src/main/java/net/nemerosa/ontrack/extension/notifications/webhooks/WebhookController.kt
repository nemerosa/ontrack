package net.nemerosa.ontrack.extension.notifications.webhooks

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/extension/notifications/webhook")
class WebhookController(
    private val webhookAdminService: WebhookAdminService,
    private val webhookExecutionService: WebhookExecutionService,
) {

    @PostMapping("{name}/ping")
    fun pingWebhook(@PathVariable name: String) {
        val webhook = webhookAdminService.findWebhookByName(name)
            ?: throw WebhookNotFoundException(name)
        val payload = WebhookPingPayloadData.pingPayload("Webhook $name ping")
        webhookExecutionService.send(webhook, payload)
    }

}