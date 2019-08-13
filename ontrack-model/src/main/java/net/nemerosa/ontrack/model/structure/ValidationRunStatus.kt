package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationRunStatus(
        val signature: Signature,
        val statusID: ValidationRunStatusID,
        val description: String?
) {

    companion object {
        @JvmStatic
        fun of(signature: Signature, validationRunStatusID: ValidationRunStatusID, description: String): ValidationRunStatus {
            return ValidationRunStatus(
                    signature,
                    validationRunStatusID,
                    description
            )
        }
    }

    @JsonProperty("passed")
    val isPassed: Boolean = statusID.isPassed

}
