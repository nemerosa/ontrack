package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GitHubPostProcessingSettingsCascIT : AbstractCascTestSupport() {

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