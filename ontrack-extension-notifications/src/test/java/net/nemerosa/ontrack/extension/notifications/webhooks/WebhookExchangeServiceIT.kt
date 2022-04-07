package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class WebhookExchangeServiceIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var webhookExchangeService: WebhookExchangeService

    @Test
    fun `Getting exchanges - no filter`() {
        asAdmin {
            val x = store()
            val y = store()
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter()
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid }, "Webhook X returned")
            assertNotNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y returned")
        }
    }

    @Test
    fun `Getting exchanges - no filter on webhook`() {
        asAdmin {
            val x = store()
            val y = store()
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter(webhook = x.webhook)
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid }, "Webhook X returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y not returned")
        }
    }

    private fun store(
        webhook: String = uid("wh"),
        requestTime: LocalDateTime = Time.now(),
        requestType: String = "event",
        requestPayload: JsonNode = mapOf("name" to "value").asJson(),
        responseTime: LocalDateTime? = null,
        responseCode: Int = 200,
        responsePayload: JsonNode = "OK".asJson(),
    ): WebhookExchange = WebhookExchange(
        uuid = UUID.randomUUID(),
        webhook = webhook,
        request = WebhookRequest(
            timestamp = requestTime,
            type = requestType,
            payload = requestPayload.format(),
        ),
        response = WebhookResponse(
            timestamp = responseTime ?: (requestTime + Duration.ofSeconds(10)),
            code = responseCode,
            payload = responsePayload.format(),
        ),
        stack = null,
    ).apply {
        webhookExchangeService.store(this)
    }

}