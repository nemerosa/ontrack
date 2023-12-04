package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.dashboards.widgets.LastActiveProjectsWidget

object DefaultDashboards {

    val defaultDashboard = Dashboard(
        uuid = "0",
        name = "Default dashboard",
        userScope = DashboardContextUserScope.BUILT_IN,
        widgets = listOf(
            WidgetInstance(
                uuid = "0",
                key = LastActiveProjectsWidget.KEY,
                config = LastActiveProjectsWidget.LastActiveProjectsWidgetConfig(count = 10).asJson(),
                layout = WidgetLayout(
                    x = 0,
                    y = 0,
                    w = 12,
                    h = 4,
                )
            )
        )
    )

    val defaultDashboards = listOf(
        defaultDashboard
    )

}
