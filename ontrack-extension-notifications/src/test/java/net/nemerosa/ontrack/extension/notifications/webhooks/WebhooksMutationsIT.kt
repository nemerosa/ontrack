package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredIntField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class WebhooksMutationsIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Test
    fun `Creating a webhook`() {
        val name = TestUtils.uid("wh")
        asAdmin {
            run("""
                mutation {
                    createWebhook(input: {
                        name: "$name",
                        enabled: true,
                        url: "uri:test",
                        timeoutSeconds: 30,
                        authenticationType: "header",
                        authenticationConfig: {
                            name: "X-Ontrack-Token",
                            value: "xxxx",
                        }
                    }) {
                        errors {
                            message
                        }
                        webhook {
                            name
                            enabled
                            url
                            timeoutSeconds
                            authenticationType
                        }
                    }
                }
            """) { data ->
                checkGraphQLUserErrors(data, "createWebhook") { payload ->
                    assertJsonNotNull(payload.path("webhook")) {
                        assertEquals(name, getRequiredTextField("name"))
                        assertEquals(true, getRequiredBooleanField("enabled"))
                        assertEquals("uri:test", getRequiredTextField("url"))
                        assertEquals(30, getRequiredIntField("timeoutSeconds"))
                    }
                }
            }
            assertNotNull(webhookAdminService.findWebhookByName(name)) {
                assertEquals(name, it.name)
                assertEquals(true, it.enabled)
                assertEquals("uri:test", it.url)
                assertEquals(Duration.ofSeconds(30), it.timeout)
            }
        }
    }

}