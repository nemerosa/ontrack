package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.dashboards.widgets.Widget

interface DashboardService {

    /**
     * Given a [dashboard context][DashboardContext], returns, if it exists,
     * the dashboard for it.
     *
     * Priority of the search is:
     *
     * * user preferences for the context [key][DashboardContext.key] and its [ID][DashboardContext.id]
     * * user preferences for the context [key][DashboardContext.key]
     * * global preferences for the context [key][DashboardContext.key] and its [ID][DashboardContext.id]
     * * global preferences for the context [key][DashboardContext.key]
     * * default for the context [key][DashboardContext.key]
     */
    fun findDashboard(
        context: DashboardContext
    ): Dashboard?

    /**
     * Updates the configuration of a widget into a dashboard.
     */
    fun updateWidgetConfig(dashboardKey: String, widgetKey: String, widgetConfig: JsonNode): Widget<*>

}