package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.yesNoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class AutoVersioningSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<AutoVersioningSettings>(
    AutoVersioningSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: AutoVersioningSettings) {
        settingsRepository.setBoolean<AutoVersioningSettings>(settings::enabled)

        // Audit retention days
        settingsRepository.setString(
            AutoVersioningSettings::class.java,
            AutoVersioningSettings::auditRetentionDuration.name,
            settings.auditRetentionDuration.toString()
        )

        // Audit cleanup days
        settingsRepository.setString(
            AutoVersioningSettings::class.java,
            AutoVersioningSettings::auditCleanupDuration.name,
            settings.auditCleanupDuration.toString()
        )

        // Build links
        settingsRepository.setBoolean<AutoVersioningSettings>(settings::buildLinks)
    }

    override fun getSettingsForm(settings: AutoVersioningSettings): Form = Form.create()
        .yesNoField(AutoVersioningSettings::enabled, settings.enabled)
        .with(
            Int.of(AutoVersioningSettings::auditRetentionDuration.name)
                .min(Duration.ofDays(1).toSeconds().toInt())
                .label("Audit retention")
                .help("Maximum number of seconds to keep non-running audit entries for auto versioning requests")
                .value(settings.auditRetentionDuration.toSeconds())
        )
        .with(
            Int.of(AutoVersioningSettings::auditCleanupDuration.name)
                .min(Duration.ofDays(1).toSeconds().toInt())
                .label("Audit cleanup milliseconds")
                .help("Maximum number of seconds to keep audit entries for auto versioning requests. This time is counted after the retention period for the non-running entries.")
                .value(settings.auditCleanupDuration.toSeconds())
        )
        .yesNoField(
            AutoVersioningSettings::buildLinks,
            settings.buildLinks
        )

    override fun getId(): String = "auto-versioning"

    override fun getTitle(): String = "Auto Versioning"
}