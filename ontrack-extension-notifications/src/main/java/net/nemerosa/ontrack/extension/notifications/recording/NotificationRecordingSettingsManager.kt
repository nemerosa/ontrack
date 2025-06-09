package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setLong
import org.springframework.stereotype.Component

@Component
class NotificationRecordingSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<NotificationRecordingSettings>(
    NotificationRecordingSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: NotificationRecordingSettings) {
        settingsRepository.setBoolean<NotificationRecordingSettings>(settings::enabled)
        settingsRepository.setLong<NotificationRecordingSettings>(settings::retentionSeconds)
        settingsRepository.setLong<NotificationRecordingSettings>(settings::cleanupIntervalSeconds)
    }

    override fun getId(): String = "notification-recordings"

    override fun getTitle(): String = "Notification recordings"
}