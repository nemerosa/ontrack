package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class GitHubSCMCatalogSettingsProvider(
        private val settingsRepository: SettingsRepository
) : SettingsProvider<GitHubSCMCatalogSettings> {

    override fun getSettings() = GitHubSCMCatalogSettings(
            orgs = settingsRepository.getString(GitHubSCMCatalogSettings::class.java, GitHubSCMCatalogSettings::orgs.name, "").split("|")
    )

    override fun getSettingsClass(): Class<GitHubSCMCatalogSettings> = GitHubSCMCatalogSettings::class.java
}