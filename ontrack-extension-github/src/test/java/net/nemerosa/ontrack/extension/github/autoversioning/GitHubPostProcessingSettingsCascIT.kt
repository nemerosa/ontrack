package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

internal class GitHubPostProcessingSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var gitHubPostProcessingSettingsCasc: GitHubPostProcessingSettingsCasc

    @Test
    fun `CasC schema type`() {
        val type = gitHubPostProcessingSettingsCasc.jsonType
        assertEquals(
            """
                {
                  "title": "GitHubPostProcessingSettings",
                  "description": null,
                  "properties": {
                    "branch": {
                      "description": "Branch to launch for the workflow",
                      "type": "string"
                    },
                    "config": {
                      "description": "Default GitHub configuration to use for the connection",
                      "type": "string"
                    },
                    "repository": {
                      "description": "Default repository (like `owner/repository`) containing the workflow to run",
                      "type": "string"
                    },
                    "retries": {
                      "description": "The amount of times we check for successful scheduling and completion of the post-processing job",
                      "type": "integer"
                    },
                    "retriesDelaySeconds": {
                      "description": "The time (in seconds) between two checks for successful scheduling and completion of the post-processing job",
                      "type": "integer"
                    },
                    "workflow": {
                      "description": "Name of the workflow containing the post-processing (like `post-processing.yml`)",
                      "type": "string"
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
    fun `Saving empty settings`() {
        asAdmin {
            withCleanSettings<GitHubPostProcessingSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-av-post-processing:
                                    config: my-org
                                    repository: my-repo
                                    workflow: post-processing.yml
                                    branch: test
                                    retries: 30
                                    retriesDelaySeconds: 10
                """.trimIndent()
                )
                val saved = cachedSettingsService.getCachedSettings(GitHubPostProcessingSettings::class.java)
                assertEquals("my-org", saved.config)
                assertEquals("my-repo", saved.repository)
                assertEquals("post-processing.yml", saved.workflow)
                assertEquals("test", saved.branch)
                assertEquals(30, saved.retries)
                assertEquals(10, saved.retriesDelaySeconds)
            }
        }
    }

}