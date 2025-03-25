package net.nemerosa.ontrack.extension.stash.settings

import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class BitbucketServerSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<BitbucketServerSettings>(
    "bitbucket-server",
    BitbucketServerSettings::class,
    settingsManagerService,
    cachedSettingsService,
)
