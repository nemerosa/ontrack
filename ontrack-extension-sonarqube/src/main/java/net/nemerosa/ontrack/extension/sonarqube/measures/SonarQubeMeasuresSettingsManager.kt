package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class SonarQubeMeasuresSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository
) : AbstractSettingsManager<SonarQubeMeasuresSettings>(
        SonarQubeMeasuresSettings::class.java,
        cachedSettingsService,
        securityService
) {

    override fun getId(): String = "sonarqube-measures"

    override fun getTitle(): String = "SonarQube measures"

    override fun doSaveSettings(settings: SonarQubeMeasuresSettings?) {
        if (settings != null) {
            settingsRepository.setString(
                    SonarQubeMeasuresSettings::class.java,
                    "measures",
                    settings.measures.joinToString("|")
            )
            settingsRepository.setBoolean(
                    SonarQubeMeasuresSettings::class.java,
                    "disabled",
                    settings.disabled
            )
            settingsRepository.setInt(
                    SonarQubeMeasuresSettings::class.java,
                    SonarQubeMeasuresSettings::coverageThreshold.name,
                    settings.coverageThreshold
            )
            settingsRepository.setInt(
                    SonarQubeMeasuresSettings::class.java,
                    SonarQubeMeasuresSettings::blockerThreshold.name,
                    settings.blockerThreshold
            )
        }
    }

}