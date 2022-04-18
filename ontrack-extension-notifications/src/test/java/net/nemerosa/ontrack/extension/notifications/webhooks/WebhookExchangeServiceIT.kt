package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class WebhookExchangeServiceIT : AbstractWebhookExchangeTestSupport() {

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
    fun `Getting exchanges - filter on webhook`() {
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

    @Test
    fun `Getting exchanges - filter on request type`() {
        asAdmin {
            val webhook = uid("wh")
            val x = store(webhook, requestType = "other")
            val y = store(webhook, requestType = "event")
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter(webhook = x.webhook, payloadType = "other")
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid }, "Webhook X with type = other returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y with type = event not returned")
        }
    }

    @Test
    fun `Getting exchanges - filter on request keyword`() {
        asAdmin {
            val webhook = uid("wh")
            val keyword = uid("kw")
            val x = store(webhook, requestPayload = mapOf("name" to keyword).asJson())
            val y = store(webhook, requestPayload = mapOf("name" to "two").asJson())
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter(webhook = x.webhook, payloadKeyword = keyword)
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid }, "Webhook X with keyword = $keyword returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y not returned")
        }
    }

    @Test
    fun `Getting exchanges - filter on response code`() {
        asAdmin {
            val webhook = uid("wh")
            val x = store(webhook, responseCode = 500)
            val y = store(webhook, responseCode = 200)
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter(webhook = x.webhook, responseCode = 500)
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid }, "Webhook X with response code = 500 returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y with response code = 200 not returned")
        }
    }

    @Test
    fun `Getting exchanges - filter on response keyword`() {
        asAdmin {
            val webhook = uid("wh")
            val keyword = uid("kw")
            val x = store(webhook, responsePayload = keyword.asJson())
            val y = store(webhook, responsePayload = "OK".asJson())
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter(webhook = webhook, responseKeyword = keyword)
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid },
                "Webhook X with response keyword = $keyword returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y not returned")
        }
    }

    @Test
    fun `Getting exchanges - filter on request timestamp`() {
        asAdmin {
            val ref = Time.now()
            val webhook = uid("wh")
            val w = store(webhook, requestTime = ref.minusSeconds(150))
            val x = store(webhook, requestTime = ref.minusSeconds(90))
            val y = store(webhook, requestTime = ref.minusSeconds(30))
            val page = webhookExchangeService.exchanges(
                WebhookExchangeFilter(
                    webhook = webhook,
                    payloadAfter = ref.minusSeconds(120),
                    payloadBefore = ref.minusSeconds(60),
                )
            )
            assertNotNull(page.pageItems.find { it.uuid == x.uuid }, "Webhook X returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook Y not returned")
            assertNull(page.pageItems.find { it.uuid == y.uuid }, "Webhook W not returned")
        }
    }

}