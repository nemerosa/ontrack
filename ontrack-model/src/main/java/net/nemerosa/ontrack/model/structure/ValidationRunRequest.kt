package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

class ValidationRunRequest
@JvmOverloads
constructor(
        @Deprecated("")
        val validationStampId: Int? = null,
        val validationStampData: ServiceConfiguration?,
        val validationStampName: String?,
        val validationRunStatusId: String,
        val description: String?,
        val properties: List<PropertyCreationRequest> = listOf()) {

    val actualValidationStampName: String?
        @JsonIgnore
        get() = validationStampData?.id ?: validationStampName

}
