package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import net.nemerosa.ontrack.model.support.getInt
import net.nemerosa.ontrack.model.support.getString
import org.springframework.stereotype.Component

@Component
class EndToEndPromotionMetricsExportSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<EndToEndPromotionMetricsExportSettings> {

    override fun getSettings() = EndToEndPromotionMetricsExportSettings(
        enabled = settingsRepository.getBoolean(
            EndToEndPromotionMetricsExportSettings::enabled,
            EndToEndPromotionMetricsExportSettings.DEFAULT_ENABLED
        ),
        branches = settingsRepository.getString(
            EndToEndPromotionMetricsExportSettings::branches,
            EndToEndPromotionMetricsExportSettings.DEFAULT_BRANCHES
        ),
        pastDays = settingsRepository.getInt(
            EndToEndPromotionMetricsExportSettings::pastDays,
            EndToEndPromotionMetricsExportSettings.DEFAULT_PAST_DAYS
        ),
    )

    override fun getSettingsClass(): Class<EndToEndPromotionMetricsExportSettings> =
        EndToEndPromotionMetricsExportSettings::class.java
}