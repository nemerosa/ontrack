package net.nemerosa.ontrack.kdsl.connector.graphql

import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.PayloadUserErrors

fun PayloadUserErrors.convert() =
    UserErrors(
        errors = this.errors()?.map {
            UserError(
                message = it.message(),
                exception = it.exception(),
                location = it.exception(),
            )
        }
    )
