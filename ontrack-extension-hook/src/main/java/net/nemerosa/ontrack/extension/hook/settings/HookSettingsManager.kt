package net.nemerosa.ontrack.extension.hook.settings

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

    override fun getSettingsForm(settings: HookSettings): Form = Form.create()
            .with(
                    Int.of(HookSettings::recordRetentionDuration.name)
                            .min(Duration.ofDays(1).toSeconds().toInt())
                            .label(getPropertyLabel(HookSettings::recordRetentionDuration))
                            .help("Maximum number of seconds to keep non-running records for queue messages")
                            .value(settings.recordRetentionDuration.toSeconds())
            )
            .with(
                    Int.of(HookSettings::recordCleanupDuration.name)
                            .min(Duration.ofDays(1).toSeconds().toInt())
                            .label(getPropertyLabel(HookSettings::recordCleanupDuration))
                            .help("Maximum number of seconds to keep any kind of records for queue messages. This time is counted after the retention period for the non-running entries.")
                            .value(settings.recordCleanupDuration.toSeconds())
            )

    override fun getId(): String = "hooks"

    override fun getTitle(): String = "Hooks"
}