package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

internal class AutoVersioningSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Minimal set of parameters`() {
        asAdmin {
            withCleanSettings<AutoVersioningSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                auto-versioning:
                                    enabled: true
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(AutoVersioningSettings::class.java)
                assertEquals(true, settings.enabled)
                assertEquals(Duration.ofDays(14), settings.auditRetentionDuration)
                assertEquals(Duration.ofDays(90), settings.auditCleanupDuration)
                assertEquals(true, settings.buildLinks)
            }
        }
    }

    @Test
    fun `Complete set of parameters`() {
        asAdmin {
            withCleanSettings<AutoVersioningSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                auto-versioning:
                                    enabled: true
                                    auditRetentionDuration: 30d
                                    auditCleanupDuration: 120d
                                    buildLinks: false
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(AutoVersioningSettings::class.java)
                assertEquals(true, settings.enabled)
                assertEquals(Duration.ofDays(30), settings.auditRetentionDuration)
                assertEquals(Duration.ofDays(120), settings.auditCleanupDuration)
                assertEquals(false, settings.buildLinks)
            }
        }
    }

}