package net.nemerosa.ontrack.model.structure

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

/**
 * 
 * Request data for the creation of a build.
 * 
 * Note that the version *without* the properties is usually the one which
 * is proposed to human clients when the one with properties is the one proposed
 * to automation clients (like CI engines).
 */
data class BuildRequest(
    @get:Pattern(
        regexp = NameDescription.NAME,
        message = "The build name " + NameDescription.NAME_MESSAGE_SUFFIX
    )
    @get:NotBlank(message = "The build name is required.")
    val name: String,
    val description: String?,
    val properties: List<PropertyCreationRequest> = emptyList(),
) {
    fun asNameDescription() = NameDescription(name, description)
}
