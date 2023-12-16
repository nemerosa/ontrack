package net.nemerosa.ontrack.graphql.payloads

fun Exception.toPayloadErrors(): List<PayloadUserError> =
    listOf(
        toPayloadError()
    )

fun Exception.toPayloadError() = PayloadUserError(
    exception = this::class.java.name,
    location = null,
    message = message ?: this::class.java.name,
)
