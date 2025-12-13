package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLTypeWebhookIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Test
    @AsAdminTest
    fun `Webhook auth config must be obfuscated`() {
        val name1 = TestUtils.uid("wh")
        webhookAdminService.createWebhook(
            name = name1,
            enabled = true,
            url = "uri:test",
            timeout = Duration.ofMinutes(1),
            authentication = WebhookFixtures.webhookAuthentication(),
        )
        run(
            """{
                webhooks(name: "$name1") {
                    name
                    authenticationType
                    authenticationConfig
                }
            }"""
        ) { data ->
            val webhook = data.path("webhooks").first()
            assertEquals("header", webhook.path("authenticationType").asText())
            val config = webhook.path("authenticationConfig")
            assertNotNull(config) {
                assertEquals("X-Ontrack-Token", it.path("name").asText())
                assertEquals("", it.path("value").asText())
            }
        }
    }

}