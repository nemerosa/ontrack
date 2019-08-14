package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationRunStatus(
        private val id: ID,
        val signature: Signature,
        val statusID: ValidationRunStatusID,
        val description: String?
) : Entity {

    override fun getId(): ID = id

    @JsonProperty("passed")
    val isPassed: Boolean = statusID.isPassed

}
