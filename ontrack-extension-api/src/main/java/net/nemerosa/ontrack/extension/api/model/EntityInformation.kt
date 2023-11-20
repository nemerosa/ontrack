package net.nemerosa.ontrack.extension.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Information returned by an [net.nemerosa.ontrack.extension.api.EntityInformationExtension].
 */
@APIDescription("Extra information component about an entity")
class EntityInformation(
        /**
         * Object which has returned the information
         */
        @get:JsonIgnore
        val extension: EntityInformationExtension,
        /**
         * Associated data
         */
        @APIDescription("Associated data")
        val data: Any
) {

    /**
     * Information type
     */
    @APIDescription("Type of information")
    val type: String = extension.javaClass.name

    /**
     * Extension feature
     */
    val feature: ExtensionFeatureDescription = extension.feature.featureDescription

}