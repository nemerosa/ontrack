package net.nemerosa.ontrack.model.dashboards.widgets

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.dashboards.WidgetInstance
import org.springframework.stereotype.Component

@Component
class LastActiveProjectsWidget : AbstractWidget<LastActiveProjectsWidget.LastActiveProjectsWidgetConfig>(
    key = "home/LastActiveProjects",
    name = "Last active projects"
) {
    data class LastActiveProjectsWidgetConfig(
        val count: Int = 10,
    ) : WidgetConfig

    fun toDefaultInstance(count: Int = 10) = WidgetInstance(
        "0",
        key,
        LastActiveProjectsWidgetConfig(count).asJson()
    )
}