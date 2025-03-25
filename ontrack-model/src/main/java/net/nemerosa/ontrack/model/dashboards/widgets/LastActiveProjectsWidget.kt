package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class LastActiveProjectsWidget : AbstractWidget<LastActiveProjectsWidget.LastActiveProjectsWidgetConfig>(
    key = KEY,
    name = "Last active projects",
    description = "Displays the list of the last active projects. The number of projects which are displayed can be configured.",
    defaultConfig = LastActiveProjectsWidgetConfig(count = 10),
    preferredHeight = 20,
) {
    data class LastActiveProjectsWidgetConfig(
        val count: Int = 10,
    ) : WidgetConfig

    companion object {
        const val KEY = "home/LastActiveProjects"
    }
}