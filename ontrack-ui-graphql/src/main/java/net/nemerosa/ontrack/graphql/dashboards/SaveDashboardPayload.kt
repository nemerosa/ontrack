package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.graphql.payloads.PayloadInterface
import net.nemerosa.ontrack.graphql.payloads.PayloadUserError
import net.nemerosa.ontrack.model.dashboards.Dashboard

class SaveDashboardPayload(
    errors: List<PayloadUserError>? = null,
    val dashboard: Dashboard? = null,
) : PayloadInterface(errors)
