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
            query = "CAST(data->>'userId' as int) = :userId AND data->'dashboard'->>'userScope' = 'PRIVATE'",
            queryVariables = mapOf("userId" to id.value)
        ).map { it.dashboard }

    override fun findSharedDashboards(): List<Dashboard> =
        storageService.filter(
            store = STORE,
            type = StoredDashboard::class,
            size = MAX_DASHBOARDS,
            query = "data->'dashboard'->>'userScope' = 'SHARED'",
        ).map { it.dashboard }

    override fun saveDashboard(dashboard: Dashboard): Dashboard {
        if (dashboard.userScope == DashboardContextUserScope.BUILT_IN) {
            error("Cannot save built-in dashboards")
        }
        storageService.store(
            STORE,
            dashboard.uuid,
            StoredDashboard(
                userId = securityService.currentUser?.account?.id(),
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

    override fun migrateDashboards(migration: (Dashboard) -> Dashboard) {
        storageService.updateAll(
            store = STORE,
            type = StoredDashboard::class,
        ) { _, item ->
            val dashboard = item.dashboard
            val migrated = migration(dashboard)
            StoredDashboard(
                userId = item.userId,
                dashboard = migrated,
            )
        }
    }

    /**
     * Stored dashboard
     *
     * @property userId Creator of the dashboard (null for backward compatibility)
     * @property dashboard Dashboard definition
     */
    private data class StoredDashboard(
        val userId: Int?,
        val dashboard: Dashboard,
    )

    companion object {
        private const val STORE = "Dashboard"
        private const val MAX_DASHBOARDS = 100
    }

}