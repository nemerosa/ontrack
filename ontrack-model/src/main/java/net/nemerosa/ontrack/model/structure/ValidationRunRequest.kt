package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

class ValidationRunRequest
@JvmOverloads
constructor(
        // FIXME #176 Validation stamp name, data type ID and data at the root
        val validationStampData: ValidationRunDataRequest? = null,
        val validationStampName: String? = null,
        val validationRunStatusId: String? = null,
        val description: String? = null,
        val properties: List<PropertyCreationRequest> = listOf()) {

    val actualValidationStampName: String
        @JsonIgnore
        get() = validationStampData?.name ?: validationStampName ?: throw IllegalStateException("Validation stamp name is required")

}
