package net.nemerosa.ontrack.extension.notifications.webhooks

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultWebhookAdminService: WebhookAdminService {
    override fun findWebhookByName(name: String): Webhook? {
        TODO("Not yet implemented")
    }
}