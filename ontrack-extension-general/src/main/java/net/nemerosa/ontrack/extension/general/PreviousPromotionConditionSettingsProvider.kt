package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class PreviousPromotionConditionSettingsProvider(
        private val settingsRepository: SettingsRepository
) : SettingsProvider<PreviousPromotionConditionSettings> {

    override fun getSettings() = PreviousPromotionConditionSettings(
            previousPromotionRequired = settingsRepository.getBoolean(PreviousPromotionConditionSettings::class.java, "previousPromotionRequired", false)
    )

    override fun getSettingsClass(): Class<PreviousPromotionConditionSettings> =
            PreviousPromotionConditionSettings::class.java
}