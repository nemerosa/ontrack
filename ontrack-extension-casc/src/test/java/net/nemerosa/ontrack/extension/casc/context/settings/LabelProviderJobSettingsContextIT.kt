package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.LabelProviderJobSettings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LabelProviderJobSettingsContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var labelProviderJobSettingsContext: LabelProviderJobSettingsContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = labelProviderJobSettingsContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "LabelProviderJobSettings",
                  "description": null,
                  "properties": {
                    "enabled": {
                      "description": "Check to enable the automated collection of labels for all projects. This can generate a high level activity in the background.",
                      "type": "boolean"
                    },
                    "interval": {
                      "description": "Interval (in minutes) between each label scan.",
                      "type": "integer"
                    },
                    "perProject": {
                      "description": "Check to have one distinct label collection job per project.",
                      "type": "boolean"
                    }
                  },
                  "required": [
                    "enabled"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Label provider job settings as CasC`() {
        asAdmin {
            withSettings<LabelProviderJobSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                label-provider-job:
                                    enabled: true
                                    interval: 120
                                    perProject: true
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(LabelProviderJobSettings::class.java)
                assertEquals(true, settings.enabled)
                assertEquals(120, settings.interval)
                assertEquals(true, settings.perProject)
            }
        }
    }

}