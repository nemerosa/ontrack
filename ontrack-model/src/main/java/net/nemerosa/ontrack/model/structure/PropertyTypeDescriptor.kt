package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Serializable variant for a property type.
 */
data class PropertyTypeDescriptor(
    val feature: ExtensionFeatureDescription,
    val typeName: String,
    val name: String,
    val description: String
) {

    companion object {
        @JvmStatic
        fun <T> of(type: PropertyType<T>) = PropertyTypeDescriptor(
            type.feature.featureDescription,
            type.javaClass.getName(),
            type.name,
            type.description
        )
    }
}
