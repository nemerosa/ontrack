package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

internal class GQLTypeWebhookExchangeIT : AbstractWebhookExchangeTestSupport() {

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Test
    fun `Webhook exchange filter on webhook`() {
        asAdmin {
            val webhook = uid("wh")
            webhookAdminService.createWebhook(
                name = webhook,
                enabled = true,
                url = "uri:test",
                timeout = Duration.ofMinutes(1),
                authentication = WebhookFixtures.webhookAuthentication(),
            )
            val ref = Time.now()
            val x = store(
                webhook = webhook,
                requestTime = ref.minusSeconds(30),
                responseTime = ref.minusSeconds(10),
            )
            /*val y = */store()
            run("""{
                webhooks(name: "$webhook") {
                    name
                    exchanges(filter: {webhook: "$webhook"}) {
                        pageItems {
                            uuid
                            webhook
                            request {
                                type
                                payload
                            }
                            response {
                                code
                                payload
                            }
                        }    
                    }
                }
            }""") { data ->
                assertEquals(
                    mapOf(
                        "webhooks" to listOf(
                            mapOf(
                                "name" to webhook,
                                "exchanges" to mapOf(
                                    "pageItems" to listOf(
                                        mapOf(
                                            "uuid" to x.uuid.toString(),
                                            "webhook" to x.webhook,
                                            "request" to mapOf(
                                                "type" to x.request.type,
                                                "payload" to x.request.payload,
                                            ),
                                            "response" to mapOf(
                                                "code" to x.response?.code,
                                                "payload" to x.response?.payload
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

}