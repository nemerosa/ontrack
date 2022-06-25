package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.intField
import net.nemerosa.ontrack.model.form.textField
import net.nemerosa.ontrack.model.form.yesNoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setInt
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class EndToEndPromotionMetricsExportSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<EndToEndPromotionMetricsExportSettings>(
    EndToEndPromotionMetricsExportSettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: EndToEndPromotionMetricsExportSettings) {
        settingsRepository.setBoolean<EndToEndPromotionMetricsExportSettings>(settings::enabled)
        settingsRepository.setString<EndToEndPromotionMetricsExportSettings>(settings::branches)
        settingsRepository.setInt<EndToEndPromotionMetricsExportSettings>(settings::pastDays)
        settingsRepository.setInt<EndToEndPromotionMetricsExportSettings>(settings::restorationDays)
    }

    override fun getSettingsForm(settings: EndToEndPromotionMetricsExportSettings): Form = Form.create()
        .yesNoField(EndToEndPromotionMetricsExportSettings::enabled, settings.enabled)
        .textField(EndToEndPromotionMetricsExportSettings::branches, settings.branches)
        .intField(EndToEndPromotionMetricsExportSettings::pastDays, settings.pastDays)
        .intField(EndToEndPromotionMetricsExportSettings::restorationDays, settings.restorationDays)

    override fun getId(): String = "end-to-end-promotion-metrics-export"

    override fun getTitle(): String = "E2E Promotion Metrics Export"
}