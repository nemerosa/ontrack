package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSettingsIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `Default settings`() {
        withSettings<SlackSettings> {
            settingsRepository.deleteAll(SlackSettings::class.java)
            val settings = settingsService.getCachedSettings(SlackSettings::class.java)
            assertFalse(settings.enabled, "Slack not enabled by default")
            assertEquals("", settings.token, "No Slack token by default")
        }
    }

    @Test
    fun `Saving the settings`() {
        asAdmin {
            withSettings<SlackSettings> {
                settingsManagerService.saveSettings(
                    SlackSettings(
                        enabled = true,
                        token = "some-token",
                    )
                )
                val settings = settingsService.getCachedSettings(SlackSettings::class.java)
                assertTrue(settings.enabled)
                assertEquals("some-token", settings.token)
            }
        }
    }

    @Test
    fun `Saving the settings without a token`() {
        asAdmin {
            withSettings<SlackSettings> {
                settingsManagerService.saveSettings(
                    SlackSettings(
                        enabled = true,
                        token = "some-token",
                    )
                )
                // Saving the settings again, without the token
                settingsManagerService.saveSettings(
                    SlackSettings(
                        enabled = true,
                        token = "",
                    )
                )
                val settings = settingsService.getCachedSettings(SlackSettings::class.java)
                assertTrue(settings.enabled)
                assertEquals("some-token", settings.token)
            }
        }
    }

}