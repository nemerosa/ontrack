package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.graphql.payloads.PayloadInterface
import net.nemerosa.ontrack.graphql.payloads.PayloadUserError

class SelectDashboardPayload(
    errors: List<PayloadUserError>? = null,
) : PayloadInterface(errors)
