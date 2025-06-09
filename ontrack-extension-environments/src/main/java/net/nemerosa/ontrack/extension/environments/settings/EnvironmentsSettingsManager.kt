package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setEnum
import org.springframework.stereotype.Component

@Component
class EnvironmentsSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<EnvironmentsSettings>(
    EnvironmentsSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: EnvironmentsSettings) {
        settingsRepository.setEnum<EnvironmentsSettings, EnvironmentsSettingsBuildDisplayOption>(settings::buildDisplayOption)
    }

    override fun getId(): String = "environments"

    override fun getTitle(): String = "Environments"

}