package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException

class DashboardCannotSaveBuiltInException: InputException(
    """Cannot save built-in dashboards."""
)

class DashboardCannotDeleteBuiltInException: InputException(
    """Cannot delete built-in dashboards."""
)

class DashboardUuidNotFoundException(uuid: String) : NotFoundException(
    "Dashboard key not found: $uuid"
)

class DashboardWidgetUuidNotFoundException(dashboard: Dashboard, uuid: String) : NotFoundException(
    "Widget $uuid not found in dashboard with key ${dashboard.uuid}"
)

class DashboardNameAlreadyExistsException(name: String): InputException(
    "Dashboard with the same name already exists: $name"
)
