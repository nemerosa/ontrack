package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.Data
import java.beans.ConstructorProperties

@Data
class ValidationRunRequest
@ConstructorProperties("validationStampId", "validationStampData", "validationStampName", "validationRunStatusId", "description", "properties")
@JvmOverloads
constructor(
        @Deprecated("")
        val validationStampId: Int? = null,
        val validationStampData: ServiceConfiguration?,
        val validationStampName: String?,
        val validationRunStatusId: String,
        val description: String,
        val properties: List<PropertyCreationRequest> = listOf()) {

    val actualValidationStampName: String?
        @JsonIgnore
        get() = validationStampData?.id ?: validationStampName

}
