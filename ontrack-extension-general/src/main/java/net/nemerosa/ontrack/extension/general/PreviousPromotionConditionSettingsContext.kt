package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class PreviousPromotionConditionSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<PreviousPromotionConditionSettings>(
    "previous-promotion-condition",
    PreviousPromotionConditionSettings::class,
    settingsManagerService,
    cachedSettingsService
)
