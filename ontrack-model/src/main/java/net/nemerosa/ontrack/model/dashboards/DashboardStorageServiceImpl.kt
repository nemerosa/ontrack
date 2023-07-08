package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service

@Service
class DashboardStorageServiceImpl(
    private val storageService: StorageService,
) : DashboardStorageService {

    override fun findDashboardByUuid(uuid: String): Dashboard? =
        storageService.find(STORE, uuid, StoredDashboard::class)?.dashboard

    override fun findDashboardsByUser(id: ID): List<Dashboard> =
        storageService.filter(
            store = STORE,
            type = StoredDashboard::class,
            size = MAX_DASHBOARDS,
            query = "data->>'userId' = :userId",
            queryVariables = mapOf("userId" to id.value)
        ).map { it.dashboard }

    override fun findSharedDashboards(): List<Dashboard> =
        storageService.filter(
            store = STORE,
            type = StoredDashboard::class,
            size = MAX_DASHBOARDS,
            query = "data->>'userId' IS NULL",
        ).map { it.dashboard }

    private data class StoredDashboard(
        val userId: Int?,
        val dashboard: Dashboard,
    )

    companion object {
        private const val STORE = "Dashboard"
        private const val MAX_DASHBOARDS = 100
    }

}