package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascField
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class SystemMessageSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<SystemMessageSettings>(
    "system-message",
    SystemMessageSettings::class,
    settingsManagerService,
    cachedSettingsService
) {

    override val type: CascType = cascObject(
        "System message settings",
        cascField(SystemMessageSettings::type),
        cascField(SystemMessageSettings::content),
    )
}