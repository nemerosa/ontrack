package net.nemerosa.ontrack.extension.artifactory.client

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration

interface ArtifactoryClientFactory {

    fun getClient(configuration: ArtifactoryConfiguration): ArtifactoryClient

}
