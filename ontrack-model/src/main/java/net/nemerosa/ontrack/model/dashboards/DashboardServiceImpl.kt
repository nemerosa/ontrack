package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.dashboards.widgets.Widget
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service

@Service
class DashboardServiceImpl(
    private val storageService: StorageService,
    private val dashboardStorageService: DashboardStorageService,
    private val defaultDashboardRegistry: DefaultDashboardRegistry,
    private val securityService: SecurityService,
) : DashboardService {

    override fun findDashboard(context: DashboardContext): Dashboard? =
        findUserDashboardByKeyAndId(context)
            ?: findUserDashboardByKey(context)
            ?: findGlobalDashboardByKeyAndId(context)
            ?: findGlobalDashboardByKey(context)
            ?: findDefaultDashboardByKey(context)

    override fun updateWidgetConfig(dashboardKey: String, widgetKey: String, widgetConfig: JsonNode): Widget<*> {
        val dashboard = findDashboard(dashboardKey)
            ?: throw DashboardKeyNotFoundException(dashboardKey)
        // Gets the widget within the dashboard
        val widget = dashboard.widgets.find {
            it.key == widgetKey
        } ?: throw DashboardWidgetKeyNotFoundException(dashboard, widgetKey)
        // TODO Validates the configuration for the widget
        // TODO Saves the dashboard with the new widget configuration
        // TODO Returns the widget
        TODO()
    }

    private fun findDefaultDashboardByKey(context: DashboardContext): Dashboard? =
        defaultDashboardRegistry.findDashboard(context.key)

    private fun findGlobalDashboardByKey(context: DashboardContext): Dashboard? =
        findDashboard(
            "global:key:${context.key}"
        )

    private fun findGlobalDashboardByKeyAndId(context: DashboardContext): Dashboard? =
        findDashboard(
            "global:id:${context.key}:${context.id}"
        )

    private fun findUserDashboardByKey(context: DashboardContext): Dashboard? =
        findDashboard(
            "user:${userId}:key:${context.key}"
        )

    private fun findUserDashboardByKeyAndId(context: DashboardContext): Dashboard? =
        findDashboard(
            "user:${userId}:id:${context.key}:${context.id}"
        )

    private val userId: String
        get() = securityService.currentAccount?.account?.id?.toString()
            ?: error("No user found")

    private fun findDashboard(key: String): Dashboard? =
        storageService.find(STORE, key, String::class)
            ?.let { dashboardKey ->
                dashboardStorageService.findDashboard(dashboardKey)
            }

    companion object {
        private const val STORE = "DashboardPreference"
    }

}