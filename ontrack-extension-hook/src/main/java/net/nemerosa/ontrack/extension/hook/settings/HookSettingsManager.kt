package net.nemerosa.ontrack.extension.hook.settings

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class HookSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<HookSettings>(
        HookSettings::class.java,
        cachedSettingsService,
        securityService
) {

    override fun doSaveSettings(settings: HookSettings) {
        settingsRepository.setString(
                HookSettings::class.java,
                HookSettings::recordRetentionDuration.name,
                settings.recordRetentionDuration.toString()
        )

        settingsRepository.setString(
                HookSettings::class.java,
                HookSettings::recordCleanupDuration.name,
                settings.recordCleanupDuration.toString()
        )

    }

    override fun getId(): String = "hooks"

    override fun getTitle(): String = "Hooks"
}