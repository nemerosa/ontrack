package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.model.settings.SettingsProvider
import org.springframework.stereotype.Component

@Component
class JenkinsPipelineLibraryIndicatorSettingsProvider: SettingsProvider<JenkinsPipelineLibraryIndicatorSettings> {

    override fun getSettings(): JenkinsPipelineLibraryIndicatorSettings {
        TODO("Not yet implemented")
    }

    override fun getSettingsClass(): Class<JenkinsPipelineLibraryIndicatorSettings> {
        TODO("Not yet implemented")
    }
}