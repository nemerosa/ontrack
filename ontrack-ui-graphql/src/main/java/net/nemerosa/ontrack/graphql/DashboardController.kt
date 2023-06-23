package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.dashboards.Dashboard
import net.nemerosa.ontrack.model.dashboards.DashboardContext
import net.nemerosa.ontrack.model.dashboards.DashboardLayouts
import net.nemerosa.ontrack.model.dashboards.DashboardService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class DashboardController(
    private val dashboardService: DashboardService,
) {

    @QueryMapping
    fun dashboardByContext(
        @Argument key: String,
        @Argument id: String,
    ): Dashboard =
        dashboardService.findDashboard(
            DashboardContext(
                key, id
            )
        ) ?: Dashboard(
            key = "nil",
            name = "Nil dashboard since none was found",
            layoutKey = DashboardLayouts.defaultLayout.key,
            widgets = emptyList(),
        )

}