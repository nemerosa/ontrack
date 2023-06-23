package net.nemerosa.ontrack.model.dashboards

interface DefaultDashboardRegistry {

    fun findDashboard(key: String): Dashboard?

}