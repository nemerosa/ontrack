package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebhookSettingsIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `Default settings`() {
        withSettings<WebhookSettings> {
            settingsRepository.deleteAll(WebhookSettings::class.java)
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