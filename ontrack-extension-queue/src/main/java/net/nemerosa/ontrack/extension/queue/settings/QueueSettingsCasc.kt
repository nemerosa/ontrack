package net.nemerosa.ontrack.extension.queue.settings

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class QueueSettingsCasc(
        settingsManagerService: SettingsManagerService,
        cachedSettingsService: CachedSettingsService,

        ) : AbstractSubSettingsContext<QueueSettings>(
        "queue",
        QueueSettings::class,
        settingsManagerService,
        cachedSettingsService
)
