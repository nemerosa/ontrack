package net.nemerosa.ontrack.service.dashboards

import net.nemerosa.ontrack.model.dashboards.Dashboard
import net.nemerosa.ontrack.model.dashboards.DashboardContextUserScope
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service

@Service
class DashboardStorageServiceImpl(
    private val securityService: SecurityService,
    private val storageService: StorageService,
) : DashboardStorageService {

    override fun findDashboardByUuid(uuid: String): Dashboard? =
        storageService.find(STORE, uuid, StoredDashboard::class)?.dashboard

    override fun findDashboardsByUser(id: ID): List<Dashboard> =
        storageService.filter(
            store = STORE,
            type = StoredDashboard::class,
            size = MAX_DASHBOARDS,
            query = "CAST(data->>'userId' as int) = :userId",
            queryVariables = mapOf("userId" to id.value)
        ).map { it.dashboard }

    override fun findSharedDashboards(): List<Dashboard> =
        storageService.filter(
            store = STORE,
            type = StoredDashboard::class,
            size = MAX_DASHBOARDS,
            query = "data->>'userId' IS NULL",
        ).map { it.dashboard }

    override fun saveDashboard(dashboard: Dashboard): Dashboard {
        val userId = if (dashboard.userScope == DashboardContextUserScope.PRIVATE) {
            securityService.currentAccount?.id()
        } else {
            null
        }
        storageService.store(
            STORE,
            dashboard.uuid,
            StoredDashboard(
                userId = userId,
                dashboard = dashboard,
            )
        )
        return dashboard
    }

    override fun ownDashboard(uuid: String, userId: ID): Boolean =
        storageService.find(STORE, uuid, StoredDashboard::class)?.run {
            this.userId == userId.value
        } ?: false

    override fun deleteDashboard(uuid: String) {
        storageService.delete(STORE, uuid)
    }

    private data class StoredDashboard(
        val userId: Int?,
        val dashboard: Dashboard,
    )

    companion object {
        private const val STORE = "Dashboard"
        private const val MAX_DASHBOARDS = 100
    }

}