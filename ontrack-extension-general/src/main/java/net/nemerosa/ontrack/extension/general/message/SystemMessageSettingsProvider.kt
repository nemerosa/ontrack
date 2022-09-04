package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.model.message.MessageType
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getEnum
import net.nemerosa.ontrack.model.support.getString
import org.springframework.stereotype.Component

@Component
class SystemMessageSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<SystemMessageSettings> {

    override fun getSettings() = SystemMessageSettings(
        content = settingsRepository.getString(SystemMessageSettings::content, ""),
        type = settingsRepository.getEnum(SystemMessageSettings::type, MessageType.INFO)
    )

    override fun getSettingsClass(): Class<SystemMessageSettings> = SystemMessageSettings::class.java
}