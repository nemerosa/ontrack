package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class AllProjectListWidget : AbstractWidget<AllProjectListWidget.AllProjectListWidgetConfig>(
    key = "home/AllProjectList",
    name = "Listing all projects",
    description = "Listing all projects, with pagination and filtering.",
    defaultConfig = AllProjectListWidgetConfig(),
    preferredHeight = 30,
) {

    class AllProjectListWidgetConfig : WidgetConfig

}