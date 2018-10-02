package net.nemerosa.ontrack.model.structure

class ValidationRunRequest
@JvmOverloads
constructor(
        val validationStampName: String,
        // FIXME #176 Replace by typed status
        val validationRunStatusId: String? = null,
        val dataTypeId: String? = null,
        val data: Any? = null,
        val description: String? = null,
        val properties: List<PropertyCreationRequest> = listOf()
)
