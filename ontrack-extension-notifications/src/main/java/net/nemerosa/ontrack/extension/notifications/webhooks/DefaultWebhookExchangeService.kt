package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultWebhookExchangeService(
    private val storageService: StorageService,
) : WebhookExchangeService {

    override fun store(webhookExchange: WebhookExchange) {
        storageService.store(
            STORE,
            webhookExchange.uuid.toString(),
            webhookExchange
        )
    }

    companion object {
        private val STORE = WebhookExchange::class.java.name
    }

}