package net.nemerosa.ontrack.service.dashboards

import net.nemerosa.ontrack.model.dashboards.*
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import org.springframework.stereotype.Service
import java.util.*

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

    override fun shareDashboard(input: ShareDashboardInput): Dashboard {
        // Checking the rights
        securityService.checkGlobalFunction(DashboardSharing::class.java)
        val userId = securityService.currentAccount?.account?.id!!
        // Gets the existing dashboard
        val existing = dashboardStorageService.findDashboardsByUser(userId)
            .find { it.uuid == input.uuid }
            ?: throw DashboardUuidNotFoundException(input.uuid)
        // Checks the scope is private
        check(existing.userScope == DashboardContextUserScope.PRIVATE) {
            "Expected private dashboard."
        }
        // Updating
        return dashboardStorageService.saveDashboard(existing.share())
    }

    override fun saveDashboard(input: SaveDashboardInput): Dashboard {
        // No built-in
        if (input.userScope == DashboardContextUserScope.BUILT_IN) {
            throw DashboardCannotSaveBuiltInException()
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
            widgets = input.widgets.map {
                WidgetInstance(
                    uuid = it.uuid
                        ?.takeIf { it.isNotBlank() }
                        ?.takeIf { !input.uuid.isNullOrBlank() }
                        ?: UUID.randomUUID().toString(),
                    key = it.key,
                    config = it.config,
                    layout = it.layout,
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

    override fun deleteDashboard(uuid: String) {
        val existing = dashboardStorageService.findDashboardByUuid(uuid)
            ?: throw DashboardUuidNotFoundException(uuid)

        when (existing.userScope) {
            DashboardContextUserScope.BUILT_IN -> throw DashboardCannotDeleteBuiltInException()
            DashboardContextUserScope.SHARED -> securityService.checkGlobalFunction(DashboardSharing::class.java)
            DashboardContextUserScope.PRIVATE -> securityService.checkGlobalFunction(DashboardEdition::class.java)
        }

        val account = securityService.currentAccount?.account
        if (account != null) {
            val prefs = preferencesService.getPreferences(account)
            if (prefs.dashboardUuid == uuid) {
                prefs.dashboardUuid = null
                preferencesService.setPreferences(account, prefs)
            }
        }

        dashboardStorageService.deleteDashboard(uuid)
    }

    override fun selectDashboard(uuid: String) {
        val accessible = userDashboards().find { it.uuid == uuid }
            ?: throw DashboardUuidNotFoundException(uuid)
        val account = securityService.currentAccount?.account
        if (account != null) {
            preferencesService.savePreferences(account) {
                it.dashboardUuid = accessible.uuid
            }
        }
    }

    override fun getAuthorizations(dashboard: Dashboard): DashboardAuthorizations {
        val account = securityService.currentAccount?.account
        return if (account != null) {
            val ownDashboard = dashboardStorageService.ownDashboard(dashboard.uuid, account.id)
            val editing = securityService.isGlobalFunctionGranted<DashboardEdition>()
            val sharing = securityService.isGlobalFunctionGranted<DashboardSharing>()
            val edit = (dashboard.userScope == DashboardContextUserScope.PRIVATE && ownDashboard && editing) ||
                    (dashboard.userScope == DashboardContextUserScope.SHARED && sharing)
            DashboardAuthorizations(
                edit = edit,
                share = dashboard.userScope == DashboardContextUserScope.PRIVATE && sharing,
                delete = edit,
            )
        } else {
            DashboardAuthorizations.NONE
        }
    }
}