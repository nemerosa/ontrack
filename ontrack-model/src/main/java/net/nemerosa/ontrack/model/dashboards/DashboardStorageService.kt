package net.nemerosa.ontrack.model.dashboards

interface DashboardStorageService {

    fun findDashboard(key: String): Dashboard?

    fun updateDashboard(key: String, updating: (Dashboard) -> Dashboard): Dashboard

}