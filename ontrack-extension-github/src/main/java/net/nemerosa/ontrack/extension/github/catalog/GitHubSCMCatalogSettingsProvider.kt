package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings.Companion.DEFAULT_AUTO_MERGE_INTERVAL
import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings.Companion.DEFAULT_AUTO_MERGE_TIMEOUT
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getLong
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class GitHubSCMCatalogSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<GitHubSCMCatalogSettings> {

    override fun getSettings() = GitHubSCMCatalogSettings(
        orgs = settingsRepository.getString(
            GitHubSCMCatalogSettings::class.java,
            GitHubSCMCatalogSettings::orgs.name,
            ""
        ).split("|"),
        autoMergeTimeout = settingsRepository.getLong(
            GitHubSCMCatalogSettings::autoMergeTimeout,
            DEFAULT_AUTO_MERGE_TIMEOUT
        ),
        autoMergeInterval = settingsRepository.getLong(
            GitHubSCMCatalogSettings::autoMergeInterval,
            DEFAULT_AUTO_MERGE_INTERVAL
        ),
    )

    override fun getSettingsClass(): Class<GitHubSCMCatalogSettings> = GitHubSCMCatalogSettings::class.java

}