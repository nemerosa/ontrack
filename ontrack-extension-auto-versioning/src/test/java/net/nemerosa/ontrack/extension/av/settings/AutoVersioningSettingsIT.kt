package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AutoVersioningSettingsIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Saving and retrieving the settings`() {
        asAdmin {
            withSettings<AutoVersioningSettings> {
                val settings = AutoVersioningSettings(
                    enabled = true,
                )
                settingsManagerService.saveSettings(settings)
                val saved = settingsService.getCachedSettings(AutoVersioningSettings::class.java)
                assertEquals(settings, saved)
            }
        }
    }

    @Test
    fun `Default settings`() {
        asAdmin {
            withSettings<AutoVersioningSettings> {
                val settings = settingsService.getCachedSettings(AutoVersioningSettings::class.java)
                assertEquals(AutoVersioningSettings.DEFAULT_ENABLED, settings.enabled)
            }
        }
    }

}