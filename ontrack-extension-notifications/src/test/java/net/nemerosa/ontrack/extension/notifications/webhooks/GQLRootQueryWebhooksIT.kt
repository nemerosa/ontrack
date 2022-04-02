package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredIntField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GQLRootQueryWebhooksIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Test
    fun `Get the list of webhooks`() {
        val name = TestUtils.uid("wh")
        asAdmin {
            webhookAdminService.createWebhook(
                name = name,
                enabled = true,
                url = "uri:test",
                timeout = Duration.ofMinutes(1),
            )
            run("""{
                webhooks {
                    name
                    enabled
                    url
                    timeoutSeconds
                }
            }""") { data ->
                val webhook = data.path("webhooks").find {
                    it.getRequiredTextField("name") == name
                }
                assertNotNull(webhook, "Found the webhook") {
                    assertEquals(name, it.getRequiredTextField("name"))
                    assertEquals(true, it.getRequiredBooleanField("enabled"))
                    assertEquals("uri:test", it.getRequiredTextField("url"))
                    assertEquals(60, it.getRequiredIntField("timeoutSeconds"))
                }
            }
        }
    }

}