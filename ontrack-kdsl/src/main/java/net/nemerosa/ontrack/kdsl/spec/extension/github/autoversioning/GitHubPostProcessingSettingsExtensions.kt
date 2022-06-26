package net.nemerosa.ontrack.kdsl.spec.extension.github.autoversioning

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.gitHubPostProcessing: SettingsInterface<GitHubPostProcessingSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "github-av-post-processing",
        type = GitHubPostProcessingSettings::class,
    )
