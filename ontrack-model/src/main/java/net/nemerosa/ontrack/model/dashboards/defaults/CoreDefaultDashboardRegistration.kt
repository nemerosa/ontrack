package net.nemerosa.ontrack.model.dashboards.defaults

import net.nemerosa.ontrack.model.dashboards.Dashboard
import net.nemerosa.ontrack.model.dashboards.DashboardLayouts
import net.nemerosa.ontrack.model.dashboards.DefaultDashboardRegistration
import net.nemerosa.ontrack.model.dashboards.widgets.home.LastActiveProjectsWidget
import net.nemerosa.ontrack.model.dashboards.widgets.project.LastActiveBranchesWidget
import net.nemerosa.ontrack.model.dashboards.widgets.project.entity.PropertiesWidget
import org.springframework.stereotype.Component

@Component
class CoreDefaultDashboardRegistration : DefaultDashboardRegistration {

    override val registrations: Map<String, Dashboard> = mapOf(
        DashboardContextKeys.HOME to Dashboard(
            key = "home-default",
            name = "Default dashboard",
            layoutKey = DashboardLayouts.defaultLayout.key,
            widgets = listOf(
                LastActiveProjectsWidget().toInstance(),
            )
        ),
        DashboardContextKeys.PROJECT to Dashboard(
            key = "project-default",
            name = "Default project dashboard",
            layoutKey = DashboardLayouts.main2ChildrenLayout.key,
            widgets = listOf(
                LastActiveBranchesWidget().toInstance(),
                PropertiesWidget().toInstance(),
            )
        )
    )
}
