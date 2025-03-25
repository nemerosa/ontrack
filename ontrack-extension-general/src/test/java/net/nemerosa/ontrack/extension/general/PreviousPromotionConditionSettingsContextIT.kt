package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreviousPromotionConditionSettingsContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var previousPromotionConditionSettingsContext: PreviousPromotionConditionSettingsContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = previousPromotionConditionSettingsContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "PreviousPromotionConditionSettings",
                  "description": null,
                  "properties": {
                    "previousPromotionRequired": {
                      "description": "Makes a promotion conditional based on the fact that a previous promotion has been granted.",
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "previousPromotionRequired"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Previous promotion condition settings as CasC`() {
        asAdmin {
            withSettings<PreviousPromotionConditionSettings> {
                casc("""
                    ontrack:
                        config:
                            settings:
                                previous-promotion-condition:
                                    previousPromotionRequired: true
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
                assertTrue(settings.previousPromotionRequired)
            }
        }
    }

}