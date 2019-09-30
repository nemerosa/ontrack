package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class SonarQubeMeasuresSettingsProvider(
        private val settingsRepository: SettingsRepository
) : SettingsProvider<SonarQubeMeasuresSettings> {

    override fun getSettings() = SonarQubeMeasuresSettings(
            measures = settingsRepository
                    .getString(SonarQubeMeasuresSettings::class.java, "measures", null)
                    ?.split("|")
                    ?: SonarQubeMeasuresSettings.DEFAULT_MEASURES,
            disabled = settingsRepository
                    .getBoolean(SonarQubeMeasuresSettings::class.java, "disabled", false)
    )

    override fun getSettingsClass(): Class<SonarQubeMeasuresSettings> =
            SonarQubeMeasuresSettings::class.java
}