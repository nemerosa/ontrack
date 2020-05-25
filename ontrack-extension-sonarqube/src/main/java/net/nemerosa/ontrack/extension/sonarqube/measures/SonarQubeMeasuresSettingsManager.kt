package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

typealias IntField = net.nemerosa.ontrack.model.form.Int

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

    override fun getSettingsForm(settings: SonarQubeMeasuresSettings?): Form {
        return Form.create()
                .with(
                        MultiStrings.of("measures")
                                .help("List of SonarQube measures to export by default.")
                                .label("Measures")
                                .value(settings?.measures ?: SonarQubeMeasuresSettings.DEFAULT_MEASURES)
                )
                .with(
                        YesNo.of("disabled")
                                .help("Check to disable the collection of SonarQube measures")
                                .label("Disable collection")
                                .value(settings?.disabled ?: false)
                )
                .with(
                        IntField.of(SonarQubeMeasuresSettings::coverageThreshold.name)
                                .help("Coverage to reach to get A rating indicator")
                                .label("Coverage threshold")
                                .min(0)
                                .max(100)
                                .value(settings?.coverageThreshold ?: 80)
                )
                .with(
                        IntField.of(SonarQubeMeasuresSettings::blockerThreshold.name)
                                .help("Maximum number of blocker issues")
                                .label("Blocker issues")
                                .min(1)
                                .value(settings?.blockerThreshold ?: 5)
                )
    }
}