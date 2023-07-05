package net.nemerosa.ontrack.model.dashboards.widgets.project

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.dashboards.WidgetInstance
import net.nemerosa.ontrack.model.dashboards.widgets.AbstractWidget
import net.nemerosa.ontrack.model.dashboards.widgets.WidgetConfig
import org.springframework.stereotype.Component

@Component
class LastActiveBranchesWidget : AbstractWidget<LastActiveBranchesWidget.LastActiveBranchesWidgetConfig>(
    key = "project/LastActiveBranches",
    name = "Last active branches"
) {
    data class LastActiveBranchesWidgetConfig(
        val count: Int = 10,
    ) : WidgetConfig

    fun toInstance(count: Int = 10) = WidgetInstance(
        "0",
        key,
        LastActiveBranchesWidgetConfig(count).asJson()
    )
}