package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.model.BitbucketCloudProject

/**
 * Interface which defines how we talk to Bitbucket Cloud.
 */
interface BitbucketCloudClient {

    /**
     * Associated workspace slug
     */
    val workspace: String

    /**
     * Gets the list of projects for this client
     *
     * @return List of projects
     */
    val projects: List<BitbucketCloudProject>

}