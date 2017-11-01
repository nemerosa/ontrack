package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Exportable description for a [ValidationDataType]
 */
data class ValidationDataTypeDescriptor(
        val feature: ExtensionFeatureDescription,
        val id: String,
        val displayName: String
)
