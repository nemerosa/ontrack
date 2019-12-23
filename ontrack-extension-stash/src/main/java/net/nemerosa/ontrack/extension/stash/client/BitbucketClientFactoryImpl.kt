package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import org.springframework.stereotype.Component

@Component
class BitbucketClientFactoryImpl : BitbucketClientFactory {
    override fun getBitbucketClient(configuration: StashConfiguration): BitbucketClient =
            BitbucketClientImpl(configuration)
}