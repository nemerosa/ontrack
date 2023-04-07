package net.nemerosa.ontrack.extension.queue.settings

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class QueueSettingsIT: AbstractDSLTestSupport() {

    @Test
    fun `Saving and reading`() {
        asAdmin {
            withCleanSettings<QueueSettings> {
                // Getting the defaults
                val defaults = cachedSettingsService.getCachedSettings(QueueSettings::class.java)
                assertEquals(QueueSettings.DEFAULT_RECORD_RETENTION_DURATION, defaults.recordRetentionDuration)
                assertEquals(QueueSettings.DEFAULT_RECORD_CLEANUP_DURATION, defaults.recordCleanupDuration)
                // Saving
                settingsManagerService.saveSettings(
                        QueueSettings(
                                recordRetentionDuration = Duration.ofDays(10),
                                recordCleanupDuration = Duration.ofDays(10),
                        )
                )
                // Reading
                val saved = cachedSettingsService.getCachedSettings(QueueSettings::class.java)
                assertEquals(Duration.ofDays(10), saved.recordRetentionDuration)
                assertEquals(Duration.ofDays(10), saved.recordCleanupDuration)
            }
        }
    }

}