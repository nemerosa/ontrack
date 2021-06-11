package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import org.springframework.stereotype.Component

@Component
class DefaultBitbucketCloudClientFactory : BitbucketCloudClientFactory {

    override fun getBitbucketCloudClient(config: BitbucketCloudConfiguration): BitbucketCloudClient =
        if (config.user != null && config.password != null) {
            DefaultBitbucketCloudClient(config.workspace, config.user!!, config.password!!)
        } else {
            throw IllegalStateException("Bitbucket Cloud user name and app password are required.")
        }

}