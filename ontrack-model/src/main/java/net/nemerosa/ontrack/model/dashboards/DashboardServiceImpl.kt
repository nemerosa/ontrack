package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service

@Service
class DashboardServiceImpl(
    private val securityService: SecurityService,
    private val preferencesService: PreferencesService,
    private val dashboardStorageService: DashboardStorageService,
) : DashboardService {

    override fun userDashboard(): Dashboard {
        val account = securityService.currentAccount
            ?: return DefaultDashboards.defaultDashboard
        val selectedUuid = preferencesService.getPreferences(account.account).dashboardUuid
            ?: return DefaultDashboards.defaultDashboard
        return dashboardStorageService.findDashboardByUuid(selectedUuid)
            ?: return DefaultDashboards.defaultDashboard
    }

    override fun userDashboards(): List<Dashboard> {
        val list = mutableListOf<Dashboard>()
        // Built-in dashboards
        list.addAll(DefaultDashboards.defaultDashboards)
        // Private dashboards
        securityService.currentAccount?.account?.let {
            list.addAll(dashboardStorageService.findDashboardsByUser(it.id))
        }
        // Shared dashboards
        list.addAll(dashboardStorageService.findSharedDashboards())
        // Storing by name
        list.sortBy { it.name }
        // OK
        return list
    }
}