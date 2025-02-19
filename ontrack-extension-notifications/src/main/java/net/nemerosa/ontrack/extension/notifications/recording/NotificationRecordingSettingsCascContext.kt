package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class NotificationRecordingSettingsCascContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
    jsonTypeBuilder: JsonTypeBuilder,
) : AbstractSubSettingsContext<NotificationRecordingSettings>(
    "notification-recordings",
    NotificationRecordingSettings::class,
    settingsManagerService,
    cachedSettingsService,
    jsonTypeBuilder,
)
