package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.BitbucketProject
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import java.time.LocalDateTime

interface BitbucketClient {

    val projects: List<BitbucketProject>

    fun getRepositories(project: BitbucketProject): List<BitbucketRepository>

    /**
     * Gets the last modified date for a given [repository][repo].
     */
    fun getRepositoryLastModified(repo: BitbucketRepository): LocalDateTime?

}