package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getBoolean
import net.nemerosa.ontrack.model.support.getPassword
import org.springframework.stereotype.Component

@Component
class TFCSettingsProvider(
    private val settingsRepository: SettingsRepository,
    private val encryptionService: EncryptionService,
) : SettingsProvider<TFCSettings> {

    override fun getSettings() = TFCSettings(
        enabled = settingsRepository.getBoolean(TFCSettings::enabled, false),
        token = settingsRepository.getPassword(TFCSettings::token, "", encryptionService::decrypt)
    )

    override fun getSettingsClass(): Class<TFCSettings> = TFCSettings::class.java
}