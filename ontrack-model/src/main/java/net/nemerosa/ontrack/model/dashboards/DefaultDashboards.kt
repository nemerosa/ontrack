package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.dashboards.widgets.LastActiveProjectsWidget

object DefaultDashboards {

    val defaultDashboard = Dashboard(
        uuid = "0",
        name = "Default dashboard",
        builtIn = true,
        userScope = DashboardContextUserScope.SHARED,
        layoutKey = DashboardLayouts.defaultLayout.key,
        widgets = listOf(
            LastActiveProjectsWidget().toDefaultInstance(),
        )
    )

    val defaultDashboards = listOf(
        defaultDashboard
    )

}
