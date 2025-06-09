package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.stereotype.Component

@Component
class PreviousPromotionConditionSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository
) : AbstractSettingsManager<PreviousPromotionConditionSettings>(
        PreviousPromotionConditionSettings::class.java,
        cachedSettingsService,
        securityService
) {

    override fun getId(): String = "previous-promotion-condition"

    override fun getTitle(): String = "Previous Promotion Conditions"

    override fun doSaveSettings(settings: PreviousPromotionConditionSettings?) {
        if (settings != null) {
            settingsRepository.setBoolean(
                    PreviousPromotionConditionSettings::class.java,
                    "previousPromotionRequired",
                    settings.previousPromotionRequired
            )
        }
    }
}