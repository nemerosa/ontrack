package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import java.util.UUID

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

    override fun saveDashboard(
        context: DashboardContext,
        userScope: DashboardContextUserScope,
        contextScope: DashboardContextScope,
        key: String?,
        name: String,
        layoutKey: String,
        widgets: List<WidgetInstance>
    ): Dashboard {
        // Checks access rights
        if (userScope == DashboardContextUserScope.GLOBAL) {
            securityService.checkGlobalFunction(DashboardSharing::class.java)
        }
        // Checks for the dashboard name unicity
        val existingByName = storageService.filter(
            STORE, Dashboard::class,
            query = "data::jsonb->>'name' = :name",
            queryVariables = mapOf("name" to name)
        ).firstOrNull()
        if (existingByName != null && (key.isNullOrBlank() || key != existingByName.key)) {
            throw DashboardNameAlreadyExistsException(name)
        }
        // TODO Validates the widget configurations
        // Saving the dashboard
        val dashboardKey = key?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val dashboard = dashboardStorageService.saveDashboard(
            Dashboard(
                key = dashboardKey,
                name = name,
                layoutKey = layoutKey,
                builtIn = false,
                widgets = widgets.map {
                    WidgetInstance(
                        uuid = it.uuid.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
                        key = it.key,
                        config = it.config,
                    )
                }
            )
        )
        // Saving for the context
        val contextKey = context.contextKey(userId, userScope, contextScope)
        storageService.store(STORE, contextKey, dashboardKey)
        // OK
        return dashboard
    }

    override fun updateWidgetConfig(dashboardKey: String, widgetUuid: String, widgetConfig: JsonNode): WidgetInstance {
        val dashboard = findDashboard(dashboardKey)
            ?: throw DashboardKeyNotFoundException(dashboardKey)
        // Gets the widget within the dashboard
        val widget = dashboard.widgets.find {
            it.uuid == widgetUuid
        } ?: throw DashboardWidgetUuidNotFoundException(dashboard, widgetUuid)
        // TODO Validates the configuration for the widget
        // Saves the dashboard with the new widget configuration
        dashboardStorageService.updateDashboard(dashboardKey) {
            it.updateWidgetConfig(widgetUuid, widgetConfig)
        }
        // Returns the updated widget instance
        return widget.updateConfig(widgetConfig)
    }

    private fun findDefaultDashboardByKey(context: DashboardContext): Dashboard? =
        defaultDashboardRegistry.findDashboard(context.key)

    private fun findGlobalDashboardByKey(context: DashboardContext): Dashboard? =
        findDashboard(
            context.contextKey(userId, DashboardContextUserScope.GLOBAL, DashboardContextScope.CONTEXT)
        )

    private fun findGlobalDashboardByKeyAndId(context: DashboardContext): Dashboard? =
        findDashboard(
            context.contextKey(userId, DashboardContextUserScope.GLOBAL, DashboardContextScope.ID)
        )

    private fun findUserDashboardByKey(context: DashboardContext): Dashboard? =
        findDashboard(
            context.contextKey(userId, DashboardContextUserScope.USER, DashboardContextScope.CONTEXT)
        )

    private fun findUserDashboardByKeyAndId(context: DashboardContext): Dashboard? =
        findDashboard(
            context.contextKey(userId, DashboardContextUserScope.USER, DashboardContextScope.ID)
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