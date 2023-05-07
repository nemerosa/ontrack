package net.nemerosa.ontrack.extension.hook.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class HookSettingsProvider(
        private val settingsRepository: SettingsRepository,
) : SettingsProvider<HookSettings> {

    override fun getSettings() = HookSettings(
            recordRetentionDuration = settingsRepository.getString(
                    HookSettings::class.java,
                    HookSettings::recordRetentionDuration.name,
                    ""
            )
                    ?.takeIf { it.isNotBlank() }
                    ?.let { Duration.parse(it) }
                    ?: HookSettings.DEFAULT_RECORD_RETENTION_DURATION,
            recordCleanupDuration = settingsRepository.getString(
                    HookSettings::class.java,
                    HookSettings::recordCleanupDuration.name,
                    ""
            )
                    ?.takeIf { it.isNotBlank() }
                    ?.let { Duration.parse(it) }
                    ?: HookSettings.DEFAULT_RECORD_CLEANUP_DURATION,
    )

    override fun getSettingsClass(): Class<HookSettings> = HookSettings::class.java
}