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
                  "title": "SampleDurationSettings",
                  "description": null,
                  "properties": {
                    "duration": {
                      "description": "duration field",
                      "type": "string",
                      "pattern": "^\\d+|P(?:\\d+Y)?(?:\\d+M)?(?:\\d+D)?(?:T(?:\\d+H)?(?:\\d+M)?(?:\\d+S)?)?|(\\d+)([smhdwMy])${'$'}"
                    }
                  },
                  "required": [
                    "duration"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Casc duration using seconds`() {
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

    @Test
    fun `Casc duration using shorthand`() {
        withSettings<SampleDurationSettings> {
            settingsRepository.deleteAll(SampleDurationSettings::class.java)
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                sample-duration:
                                    duration: 14d
                """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(SampleDurationSettings::class.java)
            assertEquals(Duration.ofDays(14), settings.duration)
        }
    }

    @Test
    fun `Casc duration using iso`() {
        withSettings<SampleDurationSettings> {
            settingsRepository.deleteAll(SampleDurationSettings::class.java)
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                sample-duration:
                                    duration: P14D
                """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(SampleDurationSettings::class.java)
            assertEquals(Duration.ofDays(14), settings.duration)
        }
    }

}