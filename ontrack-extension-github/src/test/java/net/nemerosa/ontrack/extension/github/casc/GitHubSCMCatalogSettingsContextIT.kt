package net.nemerosa.ontrack.extension.github.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitHubSCMCatalogSettingsContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var gitHubSCMCatalogSettingsContext: GitHubSCMCatalogSettingsContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = gitHubSCMCatalogSettingsContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "GitHubSCMCatalogSettings",
                  "description": null,
                  "properties": {
                    "autoMergeInterval": {
                      "description": "Number of milliseconds to wait between each auto merge control",
                      "type": "integer"
                    },
                    "autoMergeTimeout": {
                      "description": "Number of milliseconds to wait for an auto merge to be done",
                      "type": "integer"
                    },
                    "orgs": {
                      "items": {
                        "description": "orgs field",
                        "type": "string"
                      },
                      "description": "orgs field",
                      "type": "array"
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
    fun `GitHub SCM Catalog settings as CasC`() {
        asAdmin {
            withSettings<GitHubSCMCatalogSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-scm-catalog:
                                    orgs:
                                        - nemerosa
                                        - other
                                    autoMergeTimeout: 3600000
                                    autoMergeInterval: 180000
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubSCMCatalogSettings::class.java)
                assertEquals(
                    listOf(
                        "nemerosa",
                        "other"
                    ),
                    settings.orgs
                )
                assertEquals(3_600_000L, settings.autoMergeTimeout)
                assertEquals(180_000L, settings.autoMergeInterval)
            }
        }
    }

    @Test
    fun `GitHub SCM Catalog settings as CasC with default interval`() {
        asAdmin {
            withSettings<GitHubSCMCatalogSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-scm-catalog:
                                    orgs:
                                        - nemerosa
                                        - other
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubSCMCatalogSettings::class.java)
                assertEquals(
                    listOf(
                        "nemerosa",
                        "other"
                    ),
                    settings.orgs
                )
                assertEquals(600_000L, settings.autoMergeTimeout)
                assertEquals(30_000L, settings.autoMergeInterval)
            }
        }
    }

}