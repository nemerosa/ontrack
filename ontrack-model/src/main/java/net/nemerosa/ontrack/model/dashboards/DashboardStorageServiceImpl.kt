package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service

@Service
class DashboardStorageServiceImpl(
    private val storageService: StorageService,
) : DashboardStorageService {

    override fun findDashboard(key: String): Dashboard? =
        storageService.find(STORE, key, Dashboard::class)

    companion object {
        private const val STORE = "Dashboard"
    }

}