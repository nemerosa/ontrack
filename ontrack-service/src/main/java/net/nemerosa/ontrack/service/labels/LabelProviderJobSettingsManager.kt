package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class LabelProviderJobSettingsManager(
        private val settingsRepository: SettingsRepository,
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService
) : AbstractSettingsManager<LabelProviderJobSettings>(
        LabelProviderJobSettings::class.java,
        cachedSettingsService,
        securityService
) {

    override fun doSaveSettings(settings: LabelProviderJobSettings) {
        settingsRepository.setBoolean(LabelProviderJobSettings::class.java, LabelProviderJobSettings::enabled.name, settings.enabled)
        settingsRepository.setInt(LabelProviderJobSettings::class.java, LabelProviderJobSettings::interval.name, settings.interval)
        settingsRepository.setBoolean(LabelProviderJobSettings::class.java, LabelProviderJobSettings::perProject.name, settings.perProject)
    }

    override fun getId(): String = "label-provider-job"

    override fun getTitle(): String = "Label provider job"

    override fun getSettingsForm(settings: LabelProviderJobSettings): Form =
            Form.create()
                    .with(
                            YesNo.of(LabelProviderJobSettings::enabled.name)
                                    .label("Enabled")
                                    .help("Check to enable the automated collection of labels for all projects. This can generate a high level activity in the background.")
                                    .value(settings.enabled)
                    )
                    .with(
                            Int.of(LabelProviderJobSettings::interval.name)
                                    .label("Interval (minutes)")
                                    .help("Interval (in minutes) between each label scan.")
                                    .min(1)
                                    .max(kotlin.Int.MAX_VALUE)
                                    .step(1)
                                    .value(settings.interval)
                    )
                    .with(
                            YesNo.of(LabelProviderJobSettings::perProject.name)
                                    .label("Job per project")
                                    .help("Check to have one distinct label collection job per project.")
                                    .value(settings.perProject)
                    )
    
}