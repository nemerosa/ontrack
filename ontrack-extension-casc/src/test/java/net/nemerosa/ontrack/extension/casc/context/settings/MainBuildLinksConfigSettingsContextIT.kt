package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MainBuildLinksConfigSettingsContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var mainBuildLinksConfigSettingsContext: MainBuildLinksConfigSettingsContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = mainBuildLinksConfigSettingsContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "MainBuildLinksConfig",
                  "description": null,
                  "properties": {
                    "labels": {
                      "items": {
                        "description": "labels field",
                        "type": "string"
                      },
                      "description": "labels field",
                      "type": "array"
                    }
                  },
                  "required": [
                    "labels"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Main build links config as CasC`() {
        asAdmin {
            withSettings<MainBuildLinksConfig> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                main-build-links:
                                    labels:
                                        - label-1
                                        - label-2
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(MainBuildLinksConfig::class.java)
                assertEquals(
                    listOf(
                        "label-1",
                        "label-2",
                    ),
                    settings.labels
                )
            }
        }
    }

}