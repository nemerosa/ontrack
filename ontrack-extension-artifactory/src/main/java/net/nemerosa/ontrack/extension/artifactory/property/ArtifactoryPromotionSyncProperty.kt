package net.nemerosa.ontrack.extension.artifactory.property

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.json.schema.JsonSchemaString
import net.nemerosa.ontrack.model.support.ConfigurationProperty

data class ArtifactoryPromotionSyncProperty(
    /**
     * Reference to the Artifactory configuration.
     */
    @DocumentationType("String", "Name of the Artifactory configuration")
    @JsonSchemaString
    override val configuration: ArtifactoryConfiguration,

    /**
     * Artifactory build name
     */
    @APIDescription("Artifactory build name")
    val buildName: String,

    /**
     * Artifactory build name filter
     */
    @APIDescription("Artifactory build name filter")
    val buildNameFilter: String,

    /**
     * Interval between each synchronisation in minutes.
     */
    @APIDescription("Interval between each synchronisation in minutes.")
    val interval: Int,

    ) : ConfigurationProperty<ArtifactoryConfiguration>
