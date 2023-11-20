package net.nemerosa.ontrack.model.dashboards

interface DashboardService {

    /**
     * Returns the default dashboard for the current user.
     */
    fun userDashboard(): Dashboard

    /**
     * Returns the list of dashboards which are accessible to the current user.
     */
    fun userDashboards(): List<Dashboard>

    /**
     * Saves a dashboard
     */
    fun saveDashboard(input: SaveDashboardInput): Dashboard

    /**
     * Shares a dashboard
     */
    fun shareDashboard(input: ShareDashboardInput): Dashboard

    /**
     * Deletes a dashboard
     */
    fun deleteDashboard(uuid: String)

    /**
     * Selects a dashboard for the current user
     */
    fun selectDashboard(uuid: String)

    /**
     * Gets the authorizations for a given dashboard
     */
    fun getAuthorizations(dashboard: Dashboard): DashboardAuthorizations

}