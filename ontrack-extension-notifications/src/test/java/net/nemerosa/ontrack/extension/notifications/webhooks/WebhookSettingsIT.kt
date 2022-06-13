package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebhookSettingsIT : AbstractDSLTestSupport() {

    @Test
    fun `Default settings`() {
        withCleanSettings<WebhookSettings> {
            val settings = settingsService.getCachedSettings(WebhookSettings::class.java)
            assertFalse(settings.enabled, "Webhooks not enabled by default")
        }
    }

    @Test
    fun `Saving the settings`() {
        asAdmin {
            withSettings<WebhookSettings> {
                settingsManagerService.saveSettings(
                    WebhookSettings(
                        enabled = true,
                    )
                )
                val settings = settingsService.getCachedSettings(WebhookSettings::class.java)
                assertTrue(settings.enabled)
            }
        }
    }

}