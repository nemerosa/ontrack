package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.gitHubIngestion: SettingsInterface<GitHubIngestionSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "github-ingestion",
        type = GitHubIngestionSettings::class,
    )
