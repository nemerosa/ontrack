package net.nemerosa.ontrack.extension.hook.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class HookSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Complete set of parameters`() {
        asAdmin {
            withCleanSettings<HookSettings> {
                casc(
                        """
                    ontrack:
                        config:
                            settings:
                                hook:
                                    recordRetentionDuration: 30d
                                    recordCleanupDuration: 120d
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(HookSettings::class.java)
                assertEquals(Duration.ofDays(30), settings.recordRetentionDuration)
                assertEquals(Duration.ofDays(120), settings.recordCleanupDuration)
            }
        }
    }

}