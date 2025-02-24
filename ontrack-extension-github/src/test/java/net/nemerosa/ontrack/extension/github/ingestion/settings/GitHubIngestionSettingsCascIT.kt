package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitHubIngestionSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var gitHubIngestionSettingsCasc: GitHubIngestionSettingsCasc

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = gitHubIngestionSettingsCasc.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "GitHubIngestionSettings",
                  "description": null,
                  "properties": {
                    "enabled": {
                      "description": "Is the ingestion of the GitHub events enabled?",
                      "type": "boolean"
                    },
                    "indexationInterval": {
                      "description": "Default indexation interval when configuring the GitHub projects",
                      "type": "integer"
                    },
                    "issueServiceIdentifier": {
                      "description": "Identifier of the issue service to use by default. For example `self` for GitHub issues or `jira//config`.",
                      "type": "string"
                    },
                    "orgProjectPrefix": {
                      "description": "Must the organization name be used as a project name prefix?",
                      "type": "boolean"
                    },
                    "repositoryExcludes": {
                      "description": "Regular expression to exclude repositories",
                      "type": "string"
                    },
                    "repositoryIncludes": {
                      "description": "Regular expression to include repositories",
                      "type": "string"
                    },
                    "retentionDays": {
                      "description": "Number of days to keep the received payloads (0 = forever)",
                      "type": "integer"
                    },
                    "token": {
                      "description": "Secret token sent by the GitHub hook and signing the payload",
                      "type": "string"
                    }
                  },
                  "required": [
                    "token"
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
            withSettings<GitHubIngestionSettings> {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "old-token",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                        indexationInterval = 10,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-ingestion:
                                    token: new-token
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
                assertEquals("new-token", settings.token)
                assertEquals(30, settings.retentionDays)
                assertEquals(false, settings.orgProjectPrefix)
                assertEquals(30, settings.indexationInterval)
                assertEquals(".*", settings.repositoryIncludes)
                assertEquals("", settings.repositoryExcludes)
                assertEquals("self", settings.issueServiceIdentifier)
                assertEquals(true, settings.enabled)
            }
        }
    }

    @Test
    fun `All parameters`() {
        asAdmin {
            withSettings<GitHubIngestionSettings> {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "old-token",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                        indexationInterval = 10,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-ingestion:
                                    enabled: false
                                    token: new-token
                                    retentionDays: 60
                                    orgProjectPrefix: true
                                    indexationInterval: 60
                                    repositoryIncludes: "ontrack-.*"
                                    repositoryExcludes: ".*pro.*"
                                    issueServiceIdentifier: "jira//config"
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
                assertEquals("new-token", settings.token)
                assertEquals(60, settings.retentionDays)
                assertEquals(true, settings.orgProjectPrefix)
                assertEquals(60, settings.indexationInterval)
                assertEquals("ontrack-.*", settings.repositoryIncludes)
                assertEquals(".*pro.*", settings.repositoryExcludes)
                assertEquals("jira//config", settings.issueServiceIdentifier)
                assertEquals(false, settings.enabled)
            }
        }
    }

}