package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration

interface BitbucketClientFactory {

    fun getBitbucketClient(configuration: StashConfiguration): BitbucketClient

}