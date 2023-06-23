package net.nemerosa.ontrack.model.dashboards.widgets.home

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.dashboards.WidgetInstance
import net.nemerosa.ontrack.model.dashboards.widgets.AbstractWidget
import net.nemerosa.ontrack.model.dashboards.widgets.WidgetConfig
import org.springframework.stereotype.Component

@Component
class LastActiveProjectsWidget : AbstractWidget<LastActiveProjectsWidget.LastActiveProjectsWidgetConfig>(
    key = "home/LastActiveProjects",
    name = "Last active projects"
) {
    data class LastActiveProjectsWidgetConfig(
        val count: Int = 10,
    ) : WidgetConfig

    fun toInstance(count: Int = 10) = WidgetInstance(
        key,
        LastActiveProjectsWidgetConfig(count).asJson()
    )
}