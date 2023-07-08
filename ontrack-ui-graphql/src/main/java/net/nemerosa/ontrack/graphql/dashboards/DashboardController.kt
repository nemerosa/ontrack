package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.model.dashboards.Dashboard
import net.nemerosa.ontrack.model.dashboards.DashboardService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class DashboardController(
    private val dashboardService: DashboardService,
) {

    @QueryMapping
    fun userDashboard(): Dashboard = dashboardService.userDashboard()

    @QueryMapping
    fun userDashboards(): List<Dashboard> = dashboardService.userDashboards()

}