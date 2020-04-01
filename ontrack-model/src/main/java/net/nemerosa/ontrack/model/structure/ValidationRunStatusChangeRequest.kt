package net.nemerosa.ontrack.model.structure

data class ValidationRunStatusChangeRequest(
        val validationRunStatusId: String,
        val description: String?
)
