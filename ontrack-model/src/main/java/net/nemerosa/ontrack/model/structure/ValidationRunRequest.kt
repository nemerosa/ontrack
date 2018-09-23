package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

class ValidationRunRequest
@JvmOverloads
constructor(
        @Deprecated("")
        val validationStampId: Int? = null,
        val validationStampData: ServiceConfiguration? = null,
        val validationStampName: String? = null,
        val validationRunStatusId: String? = null,
        val description: String? = null,
        val properties: List<PropertyCreationRequest> = listOf()) {

    val actualValidationStampName: String?
        @JsonIgnore
        get() = validationStampData?.id ?: validationStampName

}
