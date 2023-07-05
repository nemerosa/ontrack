package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.graphql.payloads.PayloadInterface
import net.nemerosa.ontrack.graphql.payloads.PayloadUserError
import net.nemerosa.ontrack.model.dashboards.WidgetInstance

class UpdateWidgetConfigPayload(
    errors: List<PayloadUserError>? = null,
    val widget: WidgetInstance? = null,
) : PayloadInterface(errors)
