package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiStrings
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
        }
    }

    override fun getSettingsForm(settings: SonarQubeMeasuresSettings?): Form {
        return Form.create()
                .with(
                        MultiStrings.of("measures")
                                .help("List of SonarQube measures to export by default.")
                                .label("Measures")
                                .value(settings?.measures ?: SonarQubeMeasuresSettings.DEFAULT_MEASURES)
                )
    }
}