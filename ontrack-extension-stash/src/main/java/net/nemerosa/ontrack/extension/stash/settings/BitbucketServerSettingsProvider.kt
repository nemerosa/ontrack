package net.nemerosa.ontrack.extension.stash.settings

import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getLong
import org.springframework.stereotype.Component

@Component
class BitbucketServerSettingsProvider(
    private val settingsRepository: SettingsRepository,
) : SettingsProvider<BitbucketServerSettings> {

    override fun getSettings() = BitbucketServerSettings(
        autoMergeTimeout = settingsRepository.getLong(
            BitbucketServerSettings::autoMergeTimeout,
            BitbucketServerSettings.DEFAULT_AUTO_MERGE_TIMEOUT
        ),
        autoMergeInterval = settingsRepository.getLong(
            BitbucketServerSettings::autoMergeInterval,
            BitbucketServerSettings.DEFAULT_AUTO_MERGE_INTERVAL
        ),
    )

    override fun getSettingsClass(): Class<BitbucketServerSettings> = BitbucketServerSettings::class.java
}