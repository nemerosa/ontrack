package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EnvironmentsSettingsIT : AbstractDSLTestSupport() {

    @Test
    fun `Environment settings`() {
        asAdmin {
            withCleanSettings<EnvironmentsSettings> {
                // Default settings
                assertEquals(
                    EnvironmentsSettingsBuildDisplayOption.HIGHEST,
                    cachedSettingsService.getCachedSettings(EnvironmentsSettings::class.java).buildDisplayOption
                )
                // Saving the settings
                settingsManagerService.saveSettings(
                    EnvironmentsSettings(
                        buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.COUNT
                    )
                )
                assertEquals(
                    EnvironmentsSettingsBuildDisplayOption.COUNT,
                    cachedSettingsService.getCachedSettings(EnvironmentsSettings::class.java).buildDisplayOption
                )
            }
        }
    }

}