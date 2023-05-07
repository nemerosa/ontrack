package net.nemerosa.ontrack.extension.hook.settings

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class HookSettingsIT: AbstractDSLTestSupport() {

    @Test
    fun `Saving and reading`() {
        asAdmin {
            withCleanSettings<HookSettings> {
                // Getting the defaults
                val defaults = cachedSettingsService.getCachedSettings(HookSettings::class.java)
                assertEquals(HookSettings.DEFAULT_RECORD_RETENTION_DURATION, defaults.recordRetentionDuration)
                assertEquals(HookSettings.DEFAULT_RECORD_CLEANUP_DURATION, defaults.recordCleanupDuration)
                // Saving
                settingsManagerService.saveSettings(
                        HookSettings(
                                recordRetentionDuration = Duration.ofDays(10),
                                recordCleanupDuration = Duration.ofDays(10),
                        )
                )
                // Reading
                val saved = cachedSettingsService.getCachedSettings(HookSettings::class.java)
                assertEquals(Duration.ofDays(10), saved.recordRetentionDuration)
                assertEquals(Duration.ofDays(10), saved.recordCleanupDuration)
            }
        }
    }

}