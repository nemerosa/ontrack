package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getEnum
import org.springframework.stereotype.Component

@Component
class EnvironmentsSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<EnvironmentsSettings> {

    override fun getSettings() = EnvironmentsSettings(
        buildDisplayOption = settingsRepository.getEnum(
            property = EnvironmentsSettings::buildDisplayOption,
            defaultValue = EnvironmentsSettingsBuildDisplayOption.HIGHEST,
        ),
    )

    override fun getSettingsClass(): Class<EnvironmentsSettings> =
        EnvironmentsSettings::class.java

}