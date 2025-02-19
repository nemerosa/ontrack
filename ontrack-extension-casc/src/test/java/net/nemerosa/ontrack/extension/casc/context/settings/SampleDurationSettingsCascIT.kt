package net.nemerosa.ontrack.extension.casc.context.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class SampleDurationSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var sampleDurationSettingsCasc: SampleDurationSettingsCasc

    @Test
    fun `CasC schema type`() {
        val type = sampleDurationSettingsCasc.jsonType
        assertEquals(
            """
                {
                  "title": "HomePageSettings",
                  "description": null,
                  "properties": {
                    "maxBranches": {
                      "description": "Maximum of branches to display per favorite project",
                      "type": "integer"
                    },
                    "maxProjects": {
                      "description": "Maximum of projects starting from which we need to switch to a search mode",
                      "type": "integer"
                    }
                  },
                  "required": [
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Casc duration`() {
        withSettings<SampleDurationSettings> {
            settingsRepository.deleteAll(SampleDurationSettings::class.java)
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                sample-duration:
                                    duration: 1209600
                """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(SampleDurationSettings::class.java)
            assertEquals(Duration.ofDays(14), settings.duration)
        }
    }

}