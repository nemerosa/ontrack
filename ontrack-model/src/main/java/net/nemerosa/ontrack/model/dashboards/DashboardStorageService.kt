package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.structure.ID

interface DashboardStorageService {

    fun findDashboardByUuid(uuid: String): Dashboard?

    fun findDashboardsByUser(id: ID): List<Dashboard>

    fun findSharedDashboards(): List<Dashboard>

}