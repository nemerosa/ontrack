package net.nemerosa.ontrack.graphql.payloads

data class PayloadUserError(
    val exception: String,
    val location: String?,
    val message: String,
)
