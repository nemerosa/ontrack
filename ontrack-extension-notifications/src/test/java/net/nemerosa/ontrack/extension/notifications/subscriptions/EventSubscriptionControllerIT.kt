package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.webhooks.WebhookAdminService
import net.nemerosa.ontrack.extension.notifications.webhooks.WebhookFixtures
import net.nemerosa.ontrack.model.form.IdName
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.ServiceConfigurator
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertContains
import kotlin.test.assertNotNull

internal class EventSubscriptionControllerIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var eventSubscriptionController: EventSubscriptionController

    @Autowired
    private lateinit var webhookAdminService: WebhookAdminService

    @Test
    fun `Access to the subscription form from a non super admin profile`() {
        testSubscriptionFormWithGlobalRole(SubscriptionsRoleContributor.GLOBAL_SUBSCRIPTIONS_MANAGER)
        testSubscriptionFormWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR)
    }

    private fun testSubscriptionFormWithGlobalRole(role: String) {
        val whName = uid("wh")
        asAdmin {
            webhookAdminService.createWebhook(
                name = whName,
                enabled = true,
                url = "uri:test",
                timeout = Duration.ofMinutes(1),
                authentication = WebhookFixtures.webhookAuthentication(),
            )
        }
        asGlobalRole(role) {
            val form = eventSubscriptionController.create()
            // Gets the channel field
            val field = form.getField("channel") as ServiceConfigurator
            // Gets the webhook channel & the webhook name selector
            val webhook = field.sources.find { it.id == "webhook" }
            assertNotNull(webhook, "Webhook selector is present")
            val webhookSelection = webhook.form.getField("name") as Selection
            // Checks the created webhook is part of it
            assertContains(webhookSelection.items.map { (it as IdName).id }, whName)
        }
    }

}