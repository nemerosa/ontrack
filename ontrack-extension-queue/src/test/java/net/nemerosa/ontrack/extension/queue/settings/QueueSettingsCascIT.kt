package net.nemerosa.ontrack.extension.queue.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class QueueSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Complete set of parameters`() {
        asAdmin {
            withCleanSettings<QueueSettings> {
                casc(
                        """
                    ontrack:
                        config:
                            settings:
                                queue:
                                    recordRetentionDuration: 30d
                                    recordCleanupDuration: 120d
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(QueueSettings::class.java)
                assertEquals(Duration.ofDays(30), settings.recordRetentionDuration)
                assertEquals(Duration.ofDays(120), settings.recordCleanupDuration)
            }
        }
    }

}