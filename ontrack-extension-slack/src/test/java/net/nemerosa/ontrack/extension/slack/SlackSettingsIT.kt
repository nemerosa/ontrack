package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSettingsIT : AbstractDSLTestSupport() {

    @Test
    fun `Default settings`() {
        withCleanSettings<SlackSettings> {
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