package net.nemerosa.ontrack.model.structure

class ValidationRunSearchRequest(
        val branch: String?,
        val validationStamp: String?,
        val statuses: String?,
        val offset: Int,
        val size: Int
)