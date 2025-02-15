package net.nemerosa.ontrack.extension.github.ingestion

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import java.time.Duration

@TestPropertySource(
    properties = [
        "ontrack.extension.github.ingestion.processing.async=false",
    ]
)
abstract class AbstractIngestionTestSupport : AbstractGitHubTestSupport() {

    @Autowired
    protected lateinit var ingestionHookPayloadStorage: IngestionHookPayloadStorage

    protected fun withGitHubIngestionSettings(
        token: String? = IngestionHookFixtures.signatureTestToken,
        @Suppress("SameParameterValue") orgProjectPrefix: Boolean? = false,
        issueServiceIdentifier: String? = GitHubIngestionSettings.DEFAULT_ISSUE_SERVICE_IDENTIFIER,
        indexationInterval: Int? = GitHubIngestionSettings.DEFAULT_INDEXATION_INTERVAL,
        code: () -> Unit,
    ) {
        withSettings<GitHubIngestionSettings> {
            val old = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
            val new = GitHubIngestionSettings(
                token = token ?: old.token,
                retentionDays = old.retentionDays,
                orgProjectPrefix = orgProjectPrefix ?: old.orgProjectPrefix,
                indexationInterval = indexationInterval ?: old.indexationInterval,
                issueServiceIdentifier = issueServiceIdentifier ?: old.issueServiceIdentifier,
            )
            asAdmin {
                settingsManagerService.saveSettings(new)
            }
            code()
        }
    }

    protected fun onlyOneGitHubConfig(): GitHubEngineConfiguration =
        asAdmin {
            // Removing all previous configuration
            noGitHubConfig()
            // Creating one config
            gitHubConfiguration()
        }

    protected fun severalGitHubConfigs(sameRoot: Boolean = false): GitHubEngineConfiguration =
        asAdmin {
            // Removing all previous configuration
            noGitHubConfig()
            // Creating two configs, return the last one
            gitHubConfiguration(
                url = if (sameRoot) {
                    "https://github.enterprise2.com"
                } else {
                    "https://github.enterprise1.com"
                }
            )
            gitHubConfiguration(url = "https://github.enterprise2.com")
        }

    protected fun noGitHubConfig() {
        asAdmin {
            // Removing all previous configuration
            gitConfigurationService.configurations.forEach {
                gitConfigurationService.deleteConfiguration(it.name)
            }
        }
    }

    protected fun waitUntilIngestion(
        statuses: List<IngestionHookPayloadStatus>? = null,
        @Suppress("SameParameterValue")
        gitHubEvent: String? = null,
        repository: String? = null,
    ) {
        runBlocking {
            withTimeout(Duration.ofSeconds(60)) {
                var count = 0
                while (count == 0) {
                    count = asAdmin {
                        ingestionHookPayloadStorage.count(
                            statuses = statuses,
                            gitHubEvent = gitHubEvent,
                            repository = repository,
                        )
                    }
                    if (count == 0) {
                        delay(1_000)
                    }
                }
            }
        }
    }
}
