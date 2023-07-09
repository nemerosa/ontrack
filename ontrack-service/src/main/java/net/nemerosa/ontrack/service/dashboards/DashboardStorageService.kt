package net.nemerosa.ontrack.service.dashboards

import net.nemerosa.ontrack.model.dashboards.Dashboard
import net.nemerosa.ontrack.model.structure.ID

interface DashboardStorageService {

    fun findDashboardByUuid(uuid: String): Dashboard?

    fun findDashboardsByUser(id: ID): List<Dashboard>

    fun findSharedDashboards(): List<Dashboard>

    fun saveDashboard(dashboard: Dashboard): Dashboard

    fun deleteDashboard(uuid: String)

    fun ownDashboard(uuid: String, userId: ID): Boolean

}