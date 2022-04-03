package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import net.nemerosa.ontrack.model.support.getLong
import org.springframework.stereotype.Component

@Component
class NotificationRecordingSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<NotificationRecordingSettings> {

    override fun getSettings() = NotificationRecordingSettings(
        enabled = settingsRepository.getBoolean(
            NotificationRecordingSettings::enabled,
            NotificationRecordingSettings.DEFAULT_ENABLED
        ),
        retentionSeconds = settingsRepository.getLong(
            NotificationRecordingSettings::retentionSeconds,
            NotificationRecordingSettings.DEFAULT_RETENTION_SECONDS
        ),
        cleanupIntervalSeconds = settingsRepository.getLong(
            NotificationRecordingSettings::cleanupIntervalSeconds,
            NotificationRecordingSettings.DEFAULT_CLEANUP_INTERVAL_SECONDS
        ),
    )

    override fun getSettingsClass(): Class<NotificationRecordingSettings> = NotificationRecordingSettings::class.java
}