package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationRunStatus(
        override val id: ID,
        val signature: Signature,
        val statusID: ValidationRunStatusID,
        val description: String?
) : Entity {

    @JsonProperty("passed")
    val isPassed: Boolean = statusID.isPassed

}
