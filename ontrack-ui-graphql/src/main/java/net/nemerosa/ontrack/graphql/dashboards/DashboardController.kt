package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.graphql.payloads.toPayloadErrors
import net.nemerosa.ontrack.model.dashboards.Dashboard
import net.nemerosa.ontrack.model.dashboards.DashboardContext
import net.nemerosa.ontrack.model.dashboards.DashboardLayouts
import net.nemerosa.ontrack.model.dashboards.DashboardService
import net.nemerosa.ontrack.model.exceptions.InputException
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
            layoutKey = DashboardLayouts.defaultLayout.key,
            widgets = emptyList(),
        )

    /**
     * Updates the configuration of a widget for a given dashboard
     */
    @MutationMapping
    fun updateWidgetConfig(@Argument input: UpdateWidgetConfigInput): UpdateWidgetConfigPayload =
        try {
            dashboardService.updateWidgetConfig(
                dashboardKey = input.dashboardKey,
                widgetKey = input.widgetKey,
                widgetConfig = input.config,
            )?.let {
                UpdateWidgetConfigPayload(widget = it)
            }
        } catch (any: InputException) {
            UpdateWidgetConfigPayload(any.toPayloadErrors())
        }

}