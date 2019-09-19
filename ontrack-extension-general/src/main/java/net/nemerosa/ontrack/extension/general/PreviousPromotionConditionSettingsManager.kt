package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
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

    override fun getSettingsForm(settings: PreviousPromotionConditionSettings?): Form {
        return Form.create()
                .with(
                        YesNo.of("previousPromotionRequired")
                                .label("Previous promotion required")
                                .help("Makes a promotion conditional based on the fact that a previous promotion has been granted.")
                                .value(settings?.previousPromotionRequired ?: false)
                )
    }

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