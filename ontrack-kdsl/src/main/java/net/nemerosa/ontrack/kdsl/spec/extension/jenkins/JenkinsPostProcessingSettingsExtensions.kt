package net.nemerosa.ontrack.kdsl.spec.extension.jenkins

import net.nemerosa.ontrack.kdsl.spec.settings.SettingsInterface
import net.nemerosa.ontrack.kdsl.spec.settings.SettingsMgt

val SettingsMgt.jenkinsPostProcessing: SettingsInterface<JenkinsPostProcessingSettings>
    get() = SettingsInterface(
        connector = connector,
        id = "jenkins-auto-versioning-processing",
        type = JenkinsPostProcessingSettings::class,
    )
