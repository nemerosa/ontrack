package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class WebhookSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Webhooks settings using CasC`() {
        withCleanSettings<WebhookSettings> {
            casc(
                """
                ontrack:
                    config:
                        settings:
                            webhooks:
                                enabled: true
            """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(WebhookSettings::class.java)
            assertTrue(settings.enabled)
        }
    }

}