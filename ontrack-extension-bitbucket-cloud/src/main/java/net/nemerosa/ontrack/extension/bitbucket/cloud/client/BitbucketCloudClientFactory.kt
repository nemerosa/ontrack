package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration

/**
 * Creates clients to Bitbucket Cloud.
 */
interface BitbucketCloudClientFactory {

    /**
     * Given a [BitbucketCloudConfiguration], returns a client to Bitbucket Cloud.
     */
    fun getBitbucketCloudClient(config: BitbucketCloudConfiguration): BitbucketCloudClient

}