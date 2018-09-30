package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

class ValidationRunRequest
@JvmOverloads
constructor(
        @Deprecated("Editing a validation run using its stamp ID must be replaced by using its name.")
        val validationStampId: Int? = null,
        val validationStampData: ValidationRunDataRequest? = null,
        val validationStampName: String? = null,
        val validationRunStatusId: String? = null,
        val description: String? = null,
        val properties: List<PropertyCreationRequest> = listOf()) {

    val actualValidationStampName: String?
        @JsonIgnore
        get() = validationStampData?.name ?: validationStampName

}
