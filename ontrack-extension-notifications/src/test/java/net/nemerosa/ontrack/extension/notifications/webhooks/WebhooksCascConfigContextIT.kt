package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class WebhooksCascConfigContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Autowired
    private lateinit var webhooksCascConfigContext: WebhooksCascConfigContext

    @Test
    fun `CasC schema type`() {
        val type = webhooksCascConfigContext.jsonType
        assertEquals(
            """
                {
                  "items": {
                    "title": "CascWebhook",
                    "description": null,
                    "properties": {
                      "authentication": {
                        "title": "CascWebhookAuthentication",
                        "description": "Webhook authentication",
                        "properties": {
                          "config": {
                            "description": "Authentication configuration (JSON)",
                            "type": {}
                          },
                          "type": {
                            "description": "Authentication type: basic, header, bearer, ...",
                            "type": "string"
                          }
                        },
                        "required": [
                          "config",
                          "type"
                        ],
                        "additionalProperties": false,
                        "type": "object"
                      },
                      "enabled": {
                        "description": "Webhook enabled or not",
                        "type": "boolean"
                      },
                      "name": {
                        "description": "Webhook unique name",
                        "type": "string"
                      },
                      "timeoutSeconds": {
                        "description": "Webhook execution timeout (in seconds)",
                        "type": "integer"
                      },
                      "url": {
                        "description": "Webhook endpoint",
                        "type": "string"
                      }
                    },
                    "required": [
                      "authentication",
                      "name",
                      "timeoutSeconds",
                      "url"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of webhooks",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Creating a webhook using CasC`() {
        asAdmin {
            val name = uid("wh")
            casc("""
                ontrack:
                    config:
                        webhooks:
                            - name: "$name"
                              url: "https://webhook.example.com"
                              timeout-seconds: 30
                              authentication:
                                type: basic
                                config:
                                    username: my-user
                                    password: my-pass
            """.trimIndent())
            assertNotNull(webhookAdminService.findWebhookByName(name), "Webhook has been created") {
                assertEquals(name, it.name)
                assertEquals(true, it.enabled)
                assertEquals("https://webhook.example.com", it.url)
                assertEquals(30L, it.timeout.toSeconds())
                assertEquals("basic", it.authentication.type)
                assertEquals(
                    mapOf(
                        "username" to "my-user",
                        "password" to "my-pass",
                    ).asJson(),
                    it.authentication.config
                )
            }
        }
    }

    @Test
    fun `Updating a webhook using CasC`() {
        val name = uid("wh")
        asAdmin {
            webhookAdminService.createWebhook(
                name = name,
                enabled = true,
                url = "https://webhook.example.com",
                timeout = Duration.ofSeconds(30),
                authentication = WebhookAuthentication(
                    type = "bearer",
                    config = mapOf("token" to "xxx").asJson(),
                )
            )
            casc("""
                ontrack:
                    config:
                        webhooks:
                            - name: "$name"
                              url: "https://webhook.example.com"
                              timeout-seconds: 120
                              authentication:
                                type: basic
                                config:
                                    username: my-user
                                    password: my-pass
            """.trimIndent())
            assertNotNull(webhookAdminService.findWebhookByName(name), "Webhook has been created") {
                assertEquals(name, it.name)
                assertEquals(true, it.enabled)
                assertEquals("https://webhook.example.com", it.url)
                assertEquals(120L, it.timeout.toSeconds())
                assertEquals("basic", it.authentication.type)
                assertEquals(
                    mapOf(
                        "username" to "my-user",
                        "password" to "my-pass",
                    ).asJson(),
                    it.authentication.config
                )
            }
        }
    }

    @Test
    fun `Deleting a webhook using CasC`() {
        val oldName = uid("wh")
        val newName = uid("wh")
        asAdmin {
            webhookAdminService.createWebhook(
                name = oldName,
                enabled = true,
                url = "https://old-webhook.example.com",
                timeout = Duration.ofSeconds(30),
                authentication = WebhookAuthentication(
                    type = "bearer",
                    config = mapOf("token" to "xxx").asJson(),
                )
            )
            casc("""
                ontrack:
                    config:
                        webhooks:
                            - name: "$newName"
                              url: "https://webhook.example.com"
                              timeout-seconds: 30
                              authentication:
                                type: basic
                                config:
                                    username: my-user
                                    password: my-pass
            """.trimIndent())
            assertNotNull(webhookAdminService.findWebhookByName(newName), "New webhook is available")
            assertNull(webhookAdminService.findWebhookByName(oldName), "Old webhook is no longer available")
        }
    }

}