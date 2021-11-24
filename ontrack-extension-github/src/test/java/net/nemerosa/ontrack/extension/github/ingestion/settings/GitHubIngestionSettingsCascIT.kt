package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.Test
import kotlin.test.assertEquals

class GitHubIngestionSettingsCascIT : AbstractCascTestSupport() {

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
                assertEquals(".*", settings.jobIncludes)
                assertEquals("", settings.jobExcludes)
                assertEquals(".*", settings.stepIncludes)
                assertEquals("", settings.stepExcludes)
                assertEquals("self", settings.issueServiceIdentifier)
                assertEquals(true, settings.enabled)
                assertEquals(true, settings.validationJobPrefix)
                assertEquals(false, settings.runValidations)
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
                                    jobIncludes: ".*"
                                    jobExcludes: ".*prod.*"
                                    stepIncludes: ".*"
                                    stepExcludes: ".*ontrack.*"
                                    issueServiceIdentifier: "jira//config"
                                    validationJobPrefix: false
                                    runValidations: true
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
                assertEquals("new-token", settings.token)
                assertEquals(60, settings.retentionDays)
                assertEquals(true, settings.orgProjectPrefix)
                assertEquals(60, settings.indexationInterval)
                assertEquals("ontrack-.*", settings.repositoryIncludes)
                assertEquals(".*pro.*", settings.repositoryExcludes)
                assertEquals(".*", settings.jobIncludes)
                assertEquals(".*prod.*", settings.jobExcludes)
                assertEquals(".*", settings.stepIncludes)
                assertEquals(".*ontrack.*", settings.stepExcludes)
                assertEquals("jira//config", settings.issueServiceIdentifier)
                assertEquals(false, settings.enabled)
                assertEquals(false, settings.validationJobPrefix)
                assertEquals(true, settings.runValidations)
            }
        }
    }

}