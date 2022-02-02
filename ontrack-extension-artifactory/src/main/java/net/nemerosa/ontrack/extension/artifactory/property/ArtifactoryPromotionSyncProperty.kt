package net.nemerosa.ontrack.extension.artifactory.property

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration
import net.nemerosa.ontrack.model.support.ConfigurationProperty

data class ArtifactoryPromotionSyncProperty(
    /**
     * Reference to the Artifactory configuration.
     */
    override val configuration: ArtifactoryConfiguration,

    /**
     * Artifactory build name
     */
    val buildName: String,

    /**
     * Artifactory build name filter
     */
    val buildNameFilter: String,

    /**
     * Interval between each synchronisation in minutes.
     */
    val interval: Int,

    ) : ConfigurationProperty<ArtifactoryConfiguration>
