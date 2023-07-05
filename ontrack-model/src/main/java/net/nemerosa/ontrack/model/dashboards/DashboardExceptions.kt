package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class DashboardKeyNotFoundException(key: String) : NotFoundException(
    "Dashboard key not found: $key"
)

class DashboardWidgetKeyNotFoundException(dashboard: Dashboard, key: String) : NotFoundException(
    "Widget $key not found in dashboard with key ${dashboard.key}"
)
