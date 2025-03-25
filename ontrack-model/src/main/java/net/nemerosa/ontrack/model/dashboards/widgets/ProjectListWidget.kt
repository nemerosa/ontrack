package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class ProjectListWidget : AbstractWidget<ProjectListWidget.ProjectListWidgetConfig>(
    key = "home/ProjectList",
    name = "Project list",
    description = "Fixed list of projects, suitable to easily a list of projects in a dashboard.",
    defaultConfig = ProjectListWidgetConfig(),
    preferredHeight = 30,
) {

    data class ProjectListWidgetConfig(
        val projectNames: List<String> = emptyList(),
    ) : WidgetConfig

}