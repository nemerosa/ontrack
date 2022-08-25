package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.enumField
import net.nemerosa.ontrack.model.form.textField
import net.nemerosa.ontrack.model.message.MessageType
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setEnum
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class SystemMessageSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<SystemMessageSettings>(
    SystemMessageSettings::class.java,
    cachedSettingsService,
    securityService
) {

    override fun doSaveSettings(settings: SystemMessageSettings) {
        settingsRepository.setString<SystemMessageSettings>(settings::content)
        settingsRepository.setEnum<SystemMessageSettings, MessageType>(settings::type)
    }

    override fun getSettingsForm(settings: SystemMessageSettings): Form =
        Form.create()
            .textField(SystemMessageSettings::content, settings.content)
            .enumField(SystemMessageSettings::type, settings.type)

    override fun getId(): String = "system-message"

    override fun getTitle(): String = "System message"
}