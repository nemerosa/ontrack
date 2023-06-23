package net.nemerosa.ontrack.model.dashboards

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

}