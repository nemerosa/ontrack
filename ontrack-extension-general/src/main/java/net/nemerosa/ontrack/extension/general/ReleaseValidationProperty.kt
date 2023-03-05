package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * @property validation Validation to set whenever the release/label property is set.
 */
data class ReleaseValidationProperty(
    @APIDescription("Validation to set whenever the release/label property is set.")
    @APILabel("Validation")
    val validation: String,
)