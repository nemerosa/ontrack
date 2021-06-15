package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.model.BitbucketCloudProject
import net.nemerosa.ontrack.extension.bitbucket.cloud.model.BitbucketCloudRepository
import java.time.LocalDateTime

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

    /**
     * Gets all repositories for this client
     */
    val repositories: List<BitbucketCloudRepository>

    /**
     * Given a [repository], returns its last modification date (if any).
     *
     * @param repository Repository to get the date from
     * @return The last update date or `null` if not available
     */
    fun getRepositoryLastModified(repository: BitbucketCloudRepository): LocalDateTime?

    /**
     * Gets the repository information.
     *
     * @param repository Repository slug
     * @return Repository information
     */
    fun getRepository(repository: String): BitbucketCloudRepository

}