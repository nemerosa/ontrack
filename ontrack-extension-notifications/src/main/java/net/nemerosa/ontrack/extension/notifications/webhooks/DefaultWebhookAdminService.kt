package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional
class DefaultWebhookAdminService(
    private val storageService: StorageService,
    private val securityService: SecurityService,
) : WebhookAdminService {

    override fun createWebhook(name: String, enabled: Boolean, url: String, timeout: Duration): Webhook {
        securityService.checkGlobalFunction(WebhookManagement::class.java)
        val existing = findWebhookByName(name)
        if (existing != null) {
            throw WebhookAlreadyExistsException(name)
        }
        val webhook = Webhook(
            name = name,
            enabled = enabled,
            url = url,
            timeout = timeout
        )
        storageService.store(
            STORE,
            name,
            webhook
        )
        return webhook
    }

    override fun findWebhookByName(name: String): Webhook? =
        storageService.find(
            STORE,
            name,
            Webhook::class
        )

    companion object {
        private val STORE = Webhook::class.java.name
    }

}