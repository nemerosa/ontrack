package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.common.UserException
import net.nemerosa.ontrack.graphql.payloads.toPayloadErrors
import net.nemerosa.ontrack.model.dashboards.*
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class DashboardController(
    private val dashboardService: DashboardService,
) {

    @QueryMapping
    fun dashboardByContext(
        @Argument key: String,
        @Argument id: String,
    ): Dashboard =
        dashboardService.findDashboard(
            DashboardContext(
                key, id
            )
        ) ?: Dashboard(
            key = "nil",
            name = "Nil dashboard since none was found",
            builtIn = true,
            layoutKey = DashboardLayouts.defaultLayout.key,
            widgets = emptyList(),
        )

    /**
     * Saves new dashboard
     */
    @MutationMapping
    fun saveDashboard(@Argument input: SaveDashboardInput): SaveDashboardPayload? =
        try {
            dashboardService.saveDashboard(
                context = DashboardContext(input.context, input.contextId),
                userScope = input.userScope,
                contextScope = input.contextScope,
                key = input.key,
                name = input.name,
                layoutKey = input.layoutKey,
                widgets = input.widgets.map {
                    WidgetInstance(
                        uuid = it.uuid ?: "",
                        key = it.key,
                        config = it.config,
                    )
                }
            ).let {
                SaveDashboardPayload(dashboard = it)
            }
        } catch (any: UserException) {
            SaveDashboardPayload(any.toPayloadErrors())
        }

    /**
     * Updates the configuration of a widget for a given dashboard
     */
    @MutationMapping
    fun updateWidgetConfig(@Argument input: UpdateWidgetConfigInput): UpdateWidgetConfigPayload? =
        try {
            dashboardService.updateWidgetConfig(
                dashboardKey = input.dashboardKey,
                widgetUuid = input.widgetUuid,
                widgetConfig = input.config,
            ).let {
                UpdateWidgetConfigPayload(widget = it)
            }
        } catch (any: UserException) {
            UpdateWidgetConfigPayload(any.toPayloadErrors())
        }

}