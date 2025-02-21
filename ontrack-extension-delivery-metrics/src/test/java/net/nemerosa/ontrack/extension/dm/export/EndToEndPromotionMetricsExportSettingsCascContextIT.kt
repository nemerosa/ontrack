package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EndToEndPromotionMetricsExportSettingsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var endToEndPromotionMetricsExportSettingsCascContext: EndToEndPromotionMetricsExportSettingsCascContext

    @Test
    fun `CasC schema type`() {
        val type = endToEndPromotionMetricsExportSettingsCascContext.jsonType
        assertEquals(
            """
                {
                  "title": "EndToEndPromotionMetricsExportSettings",
                  "description": null,
                  "properties": {
                    "branches": {
                      "description": "Regex for the branches eligible for the export",
                      "type": "string"
                    },
                    "enabled": {
                      "description": "Export enabled",
                      "type": "boolean"
                    },
                    "pastDays": {
                      "description": "Number of days in the past when looking for event metrics",
                      "type": "integer"
                    },
                    "restorationDays": {
                      "description": "Number of days in the past to restore",
                      "type": "integer"
                    }
                  },
                  "required": [],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

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