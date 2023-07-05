package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class DashboardKeyNotFoundException(key: String) : NotFoundException(
    "Dashboard key not found: $key"
)

class DashboardWidgetUuidNotFoundException(dashboard: Dashboard, uuid: String) : NotFoundException(
    "Widget $uuid not found in dashboard with key ${dashboard.key}"
)
