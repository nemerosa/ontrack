package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class EndToEndPromotionMetricsExportSettingsCascContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<EndToEndPromotionMetricsExportSettings>(
    "e2e-promotion-metrics",
    EndToEndPromotionMetricsExportSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
