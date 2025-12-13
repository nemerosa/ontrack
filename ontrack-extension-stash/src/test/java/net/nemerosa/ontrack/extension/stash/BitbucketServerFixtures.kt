package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration

object BitbucketServerFixtures {

    fun bitbucketServerConfig(
        name: String = "Bitbucket",
        url: String = BITBUCKET_SERVER_URL,
    ) = StashConfiguration(
        name = name,
        url = url,
        user = "user",
        password = "secret",
        autoMergeUser = null,
        autoMergeToken = null
    )

    const val BITBUCKET_SERVER_URL = "https://bitbucket.dev.yontrack.com"

}