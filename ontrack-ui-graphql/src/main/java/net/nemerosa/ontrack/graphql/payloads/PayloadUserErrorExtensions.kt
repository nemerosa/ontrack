package net.nemerosa.ontrack.graphql.payloads

import net.nemerosa.ontrack.common.UserException

fun UserException.toPayloadErrors(): List<PayloadUserError> =
    listOf(
        toPayloadError()
    )

fun UserException.toPayloadError() = PayloadUserError(
    exception = this::class.java.name,
    location = null,
    message = message ?: this::class.java.name,
)
