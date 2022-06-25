package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EndToEndPromotionMetricsExportSettingsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun `End to end promotion metrics export settings using CasC`() {
        withSettings<EndToEndPromotionMetricsExportSettings> {
            settingsRepository.deleteAll(EndToEndPromotionMetricsExportSettings::class.java)
            casc(
                """
                ontrack:
                    config:
                        settings:
                            e2e-promotion-metrics:
                                enabled: true
                                branches: "develop|main|master|release-.*|maintenance-.*"
                                pastDays: 14
                                restorationDays: 730
            """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(EndToEndPromotionMetricsExportSettings::class.java)
            assertTrue(settings.enabled)
            assertEquals("develop|main|master|release-.*|maintenance-.*", settings.branches)
            assertEquals(14, settings.pastDays)
            assertEquals(730, settings.restorationDays)
        }
    }

}