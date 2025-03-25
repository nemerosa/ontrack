package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

internal class JenkinsPostProcessingSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var jenkinsPostProcessingSettingsCasc: JenkinsPostProcessingSettingsCasc

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = jenkinsPostProcessingSettingsCasc.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "JenkinsPostProcessingSettings",
                  "description": null,
                  "properties": {
                    "config": {
                      "description": "Default Jenkins configuration to use for the connection",
                      "type": "string"
                    },
                    "job": {
                      "description": "Default path to the job to launch for the post-processing, relative to the Jenkins root URL (note that `/job/` separators can be omitted)",
                      "type": "string"
                    },
                    "retries": {
                      "description": "The amount of times we check for successful scheduling and completion of the post-processing job",
                      "type": "integer"
                    },
                    "retriesDelaySeconds": {
                      "description": "The time (in seconds) between two checks for successful scheduling and completion of the post-processing job",
                      "type": "integer"
                    }
                  },
                  "required": [
                    "config",
                    "job"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Minimal parameters`() {
        asAdmin {
            withCleanSettings<JenkinsPostProcessingSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                auto-versioning-jenkins:
                                    config: my-config
                                    job: path/to/job
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(JenkinsPostProcessingSettings::class.java)
                assertEquals("my-config", settings.config)
                assertEquals("path/to/job", settings.job)
                assertEquals(10, settings.retries)
                assertEquals(30, settings.retriesDelaySeconds)
            }
        }
    }

    @Test
    fun `Full parameters`() {
        asAdmin {
            withCleanSettings<JenkinsPostProcessingSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                auto-versioning-jenkins:
                                    config: my-config
                                    job: path/to/job
                                    retries: 3
                                    retriesDelaySeconds: 600
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(JenkinsPostProcessingSettings::class.java)
                assertEquals("my-config", settings.config)
                assertEquals("path/to/job", settings.job)
                assertEquals(3, settings.retries)
                assertEquals(600, settings.retriesDelaySeconds)
            }
        }
    }

}