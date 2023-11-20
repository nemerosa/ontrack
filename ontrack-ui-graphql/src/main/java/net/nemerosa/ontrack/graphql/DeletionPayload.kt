package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.graphql.payloads.PayloadInterface
import net.nemerosa.ontrack.graphql.payloads.PayloadUserError

class DeletionPayload(
    errors: List<PayloadUserError>? = null,
) : PayloadInterface(errors)
