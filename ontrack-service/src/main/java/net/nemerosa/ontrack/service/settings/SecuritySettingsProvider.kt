package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class SecuritySettingsProvider(
        private val settingsRepository: SettingsRepository
) : SettingsProvider<SecuritySettings> {
    /**
     * By default, grants view accesses to everybody.
     */
    override fun getSettings(): SecuritySettings = SecuritySettings(
            settingsRepository.getBoolean(SecuritySettings::class.java, "grantProjectViewToAll", true),
            settingsRepository.getBoolean(SecuritySettings::class.java, "grantProjectParticipationToAll", true),
            settingsRepository.getBoolean(SecuritySettings::class.java, SecuritySettings::builtInAuthenticationEnabled.name, SecuritySettings.DEFAULT_BUILTIN_AUTHENTICATION_ENABLED),
            settingsRepository.getBoolean(SecuritySettings::class.java, SecuritySettings::grantDashboardEditionToAll.name, SecuritySettings.DEFAULT_GRANT_DASHBOARD_EDITION),
            settingsRepository.getBoolean(SecuritySettings::class.java, SecuritySettings::grantDashboardSharingToAll.name, SecuritySettings.DEFAULT_GRANT_DASHBOARD_SHARING),
    )

    override fun getSettingsClass(): Class<SecuritySettings> = SecuritySettings::class.java

}