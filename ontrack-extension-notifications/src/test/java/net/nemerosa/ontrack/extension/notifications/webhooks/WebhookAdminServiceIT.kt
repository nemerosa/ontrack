package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class WebhookAdminServiceIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Test
    fun `Creating a webhook`() {
        val name = uid("wh")
        asAdmin {
            val wh = webhookAdminService.createWebhook(
                name = name,
                enabled = true,
                url = "uri:test",
                timeout = Duration.ofMinutes(1),
            )
            assertNotNull(webhookAdminService.findWebhookByName(name)) {
                assertEquals(wh, it)
            }
        }
    }

    @Test
    fun `Creating a webhook with existing name`() {
        val name = uid("wh")
        asAdmin {
            webhookAdminService.createWebhook(
                name = name,
                enabled = true,
                url = "uri:test",
                timeout = Duration.ofMinutes(1),
            )
            // Creating a webhook with the same name
            assertFailsWith<WebhookAlreadyExistsException> {
                webhookAdminService.createWebhook(
                    name = name,
                    enabled = true,
                    url = "uri:other",
                    timeout = Duration.ofMinutes(1),
                )
            }
        }
    }

}