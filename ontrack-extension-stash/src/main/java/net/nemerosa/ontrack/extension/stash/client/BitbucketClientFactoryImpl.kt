package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.settings.BitbucketServerSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
class BitbucketClientFactoryImpl(
    private val cachedSettingsService: CachedSettingsService,
) : BitbucketClientFactory {
    override fun getBitbucketClient(configuration: StashConfiguration): BitbucketClient {
        val settings = cachedSettingsService.getCachedSettings(BitbucketServerSettings::class.java)
        return BitbucketClientImpl(configuration, maxCommits = settings.maxCommits)
    }
}