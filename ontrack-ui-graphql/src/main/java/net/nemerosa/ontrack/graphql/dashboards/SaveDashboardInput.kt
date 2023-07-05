package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.model.dashboards.DashboardContextScope
import net.nemerosa.ontrack.model.dashboards.DashboardContextUserScope

data class SaveDashboardInput(
    val context: String,
    val contextId: String,
    val userScope: DashboardContextUserScope,
    val contextScope: DashboardContextScope,
    val key: String?,
    val name: String,
    val layoutKey: String,
    val widgets: List<WidgetInstanceInput>,
)
