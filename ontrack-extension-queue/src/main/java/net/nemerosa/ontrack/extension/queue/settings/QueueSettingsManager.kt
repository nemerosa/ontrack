package net.nemerosa.ontrack.extension.queue.settings

import net.nemerosa.ontrack.model.annotations.getPropertyLabel
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class QueueSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<QueueSettings>(
        QueueSettings::class.java,
        cachedSettingsService,
        securityService
) {

    override fun doSaveSettings(settings: QueueSettings) {
        settingsRepository.setString(
                QueueSettings::class.java,
                QueueSettings::recordRetentionDuration.name,
                settings.recordRetentionDuration.toString()
        )

        settingsRepository.setString(
                QueueSettings::class.java,
                QueueSettings::recordCleanupDuration.name,
                settings.recordCleanupDuration.toString()
        )

    }

    override fun getSettingsForm(settings: QueueSettings): Form = Form.create()
            .with(
                    Int.of(QueueSettings::recordRetentionDuration.name)
                            .min(Duration.ofDays(1).toSeconds().toInt())
                            .label(getPropertyLabel(QueueSettings::recordRetentionDuration))
                            .help("Maximum number of seconds to keep non-running records for queue messages")
                            .value(settings.recordRetentionDuration.toSeconds())
            )
            .with(
                    Int.of(QueueSettings::recordCleanupDuration.name)
                            .min(Duration.ofDays(1).toSeconds().toInt())
                            .label(getPropertyLabel(QueueSettings::recordCleanupDuration))
                            .help("Maximum number of seconds to keep any kind of records for queue messages. This time is counted after the retention period for the non-running entries.")
                            .value(settings.recordCleanupDuration.toSeconds())
            )

    override fun getId(): String = "queue"

    override fun getTitle(): String = "Queue management"
}