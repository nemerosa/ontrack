package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class SecuritySettingsManager(
        cachedSettingsService: CachedSettingsService,
        private val settingsRepository: SettingsRepository,
        securityService: SecurityService
) : AbstractSettingsManager<SecuritySettings>(SecuritySettings::class.java, cachedSettingsService, securityService) {

    override fun doSaveSettings(settings: SecuritySettings) {
        settingsRepository.setBoolean(SecuritySettings::class.java, "grantProjectViewToAll", settings.isGrantProjectViewToAll)
        settingsRepository.setBoolean(SecuritySettings::class.java, "grantProjectParticipationToAll", settings.isGrantProjectParticipationToAll)
        settingsRepository.setBoolean(SecuritySettings::class.java, SecuritySettings::builtInAuthenticationEnabled.name, settings.builtInAuthenticationEnabled)
        settingsRepository.setBoolean(SecuritySettings::class.java, SecuritySettings::grantDashboardEditionToAll.name, settings.grantDashboardEditionToAll)
        settingsRepository.setBoolean(SecuritySettings::class.java, SecuritySettings::grantDashboardSharingToAll.name, settings.grantDashboardSharingToAll)
    }

    override fun getId(): String = "general-security"

    override fun getTitle(): String = "General security settings"

}