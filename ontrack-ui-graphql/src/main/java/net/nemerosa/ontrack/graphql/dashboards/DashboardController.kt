package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.common.UserException
import net.nemerosa.ontrack.graphql.payloads.toPayloadErrors
import net.nemerosa.ontrack.model.dashboards.*
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
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

    @QueryMapping
    fun dashboardLayouts(): List<DashboardLayout> = DashboardLayouts.layouts

    @MutationMapping
    fun saveDashboard(@Argument input: SaveDashboardInput): SaveDashboardPayload =
        try {
            SaveDashboardPayload(dashboard = dashboardService.saveDashboard(input))
        } catch (any: UserException) {
            SaveDashboardPayload(any.toPayloadErrors())
        }

}