package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class WebhookSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `Webhooks settings using CasC`() {
        withSettings<WebhookSettings> {
            settingsRepository.deleteAll(WebhookSettings::class.java)
            casc("""
                ontrack:
                    config:
                        settings:
                            webhooks:
                                enabled: true
            """.trimIndent())
            val settings = settingsService.getCachedSettings(WebhookSettings::class.java)
            assertTrue(settings.enabled)
        }
    }

}