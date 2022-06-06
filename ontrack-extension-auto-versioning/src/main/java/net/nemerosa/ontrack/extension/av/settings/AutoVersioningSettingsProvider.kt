package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class AutoVersioningSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<AutoVersioningSettings> {

    override fun getSettings() = AutoVersioningSettings(
        enabled = settingsRepository.getBoolean(
            AutoVersioningSettings::enabled,
            AutoVersioningSettings.DEFAULT_ENABLED
        ),
        auditRetentionDuration = settingsRepository.getString(
            AutoVersioningSettings::class.java,
            AutoVersioningSettings::auditRetentionDuration.name,
            ""
        )
            ?.takeIf { it.isNotBlank() }
            ?.let { Duration.parse(it) }
            ?: AutoVersioningSettings.DEFAULT_AUDIT_RETENTION_DURATION,
        auditCleanupDuration = settingsRepository.getString(
            AutoVersioningSettings::class.java,
            AutoVersioningSettings::auditCleanupDuration.name,
            ""
        )
            ?.takeIf { it.isNotBlank() }
            ?.let { Duration.parse(it) }
            ?: AutoVersioningSettings.DEFAULT_AUDIT_CLEANUP_DURATION,
    )

    override fun getSettingsClass(): Class<AutoVersioningSettings> = AutoVersioningSettings::class.java
}