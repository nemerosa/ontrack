package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.BitbucketProject
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository

interface BitbucketClient {

    val projects: List<BitbucketProject>

    fun getRepositories(project: BitbucketProject): List<BitbucketRepository>

}