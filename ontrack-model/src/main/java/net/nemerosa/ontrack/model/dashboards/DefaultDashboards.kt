package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.dashboards.widgets.LastActiveProjectsWidget

object DefaultDashboards {

    val defaultDashboard = Dashboard(
        uuid = "0",
        name = "Default dashboard",
        userScope = DashboardContextUserScope.BUILT_IN,
        layoutKey = DashboardLayouts.defaultLayout.key,
        widgets = listOf(
            WidgetInstance.fromDefaultWidget("0", LastActiveProjectsWidget())
        )
    )

    val defaultDashboards = listOf(
        defaultDashboard
    )

}
