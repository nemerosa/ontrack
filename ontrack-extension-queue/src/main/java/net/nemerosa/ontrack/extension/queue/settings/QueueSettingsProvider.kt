package net.nemerosa.ontrack.extension.queue.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class QueueSettingsProvider(
        private val settingsRepository: SettingsRepository,
) : SettingsProvider<QueueSettings> {

    override fun getSettings() = QueueSettings(
            recordRetentionDuration = settingsRepository.getString(
                    QueueSettings::class.java,
                    QueueSettings::recordRetentionDuration.name,
                    ""
            )
                    ?.takeIf { it.isNotBlank() }
                    ?.let { Duration.parse(it) }
                    ?: QueueSettings.DEFAULT_RECORD_RETENTION_DURATION,
            recordCleanupDuration = settingsRepository.getString(
                    QueueSettings::class.java,
                    QueueSettings::recordCleanupDuration.name,
                    ""
            )
                    ?.takeIf { it.isNotBlank() }
                    ?.let { Duration.parse(it) }
                    ?: QueueSettings.DEFAULT_RECORD_CLEANUP_DURATION,
    )

    override fun getSettingsClass(): Class<QueueSettings> = QueueSettings::class.java
}