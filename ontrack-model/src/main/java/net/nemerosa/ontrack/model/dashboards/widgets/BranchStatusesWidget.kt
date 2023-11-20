package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component
import java.time.Duration

@Component
class BranchStatusesWidget: AbstractWidget<BranchStatusesWidgetConfig>(
    key = "home/BranchStatuses",
    name = "Branch statuses",
    description = "Displays the promotions & validations for a set of branches",
    defaultConfig = BranchStatusesWidgetConfig(),
) {
}

data class BranchStatusesWidgetConfig(
    val title: String = "Branch statuses",
    val promotions: List<String> = emptyList(),
    val validations: List<String> = emptyList(),
    val branches: List<BranchStatusesWidgetConfigBranch> = emptyList(),
    val refreshInterval: Duration = Duration.ZERO,
): WidgetConfig

data class BranchStatusesWidgetConfigBranch(
    val project: String,
    val branch: String,
)
