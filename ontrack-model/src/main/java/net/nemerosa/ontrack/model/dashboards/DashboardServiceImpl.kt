package net.nemerosa.ontrack.model.dashboards

import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import java.util.UUID

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

    override fun saveDashboard(input: SaveDashboardInput): Dashboard {
        // No built-in
        if (input.userScope == DashboardContextUserScope.BUILT_IN) {
            throw DashboardCannotBuiltInException()
        }
        // Checks rights to edit
        securityService.checkGlobalFunction(DashboardEdition::class.java)
        // Checks rights to share
        if (input.userScope != DashboardContextUserScope.PRIVATE) {
            securityService.checkGlobalFunction(DashboardSharing::class.java)
        }
        // Checks name unicity among accessible dashboards
        val existingDashboards = userDashboards()
        val existingDashboard = existingDashboards.find { it.name == input.name }
        if (existingDashboard != null && (input.uuid.isNullOrBlank() || input.uuid != existingDashboard.uuid)) {
            throw DashboardNameAlreadyExistsException(input.name)
        }
        // TODO Checks the widgets configurations
        // Fills the IDs
        val dashboard = Dashboard(
            uuid = input.uuid?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
            name = input.name,
            userScope = input.userScope,
            layoutKey = input.layoutKey,
            widgets = input.widgets.map {
                WidgetInstance(
                    uuid = it.uuid
                        ?.takeIf { it.isNotBlank() }
                        ?.takeIf { !input.uuid.isNullOrBlank() }
                        ?: UUID.randomUUID().toString(),
                    key = it.key,
                    config = it.config,
                )
            }
        )
        // Saves the dashboard
        dashboardStorageService.saveDashboard(dashboard)
        // Selection for the user
        if (input.select) {
            val account = securityService.currentAccount?.account
            if (account != null) {
                preferencesService.savePreferences(account) {
                    it.dashboardUuid = dashboard.uuid
                }
            }
        }
        // OK
        return dashboard
    }
}