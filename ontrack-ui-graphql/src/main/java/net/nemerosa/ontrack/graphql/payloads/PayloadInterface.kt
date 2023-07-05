package net.nemerosa.ontrack.graphql.payloads

abstract class PayloadInterface(
    val errors: List<PayloadUserError>? = null,
)
