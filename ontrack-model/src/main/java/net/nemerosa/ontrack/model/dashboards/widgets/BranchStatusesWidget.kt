package net.nemerosa.ontrack.model.dashboards.widgets

import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class BranchStatusesWidget : AbstractWidget<BranchStatusesWidgetConfig>(
    key = "home/BranchStatuses",
    name = "Branch statuses",
    description = "Displays the promotions & validations for a set of branches",
    defaultConfig = BranchStatusesWidgetConfig(),
    preferredHeight = 6,
)

data class BranchStatusesWidgetConfig(
    val title: String = "Branch statuses",
    val promotions: List<String> = emptyList(),
    val validations: List<String> = emptyList(),
    val branches: List<BranchStatusesWidgetConfigBranch> = emptyList(),
    val refreshInterval: Duration = Duration.ZERO,
    @APIDescription("If checked, displays additional results with each validation, like test summary, etc.")
    val displayValidationResults: Boolean = false,
    @APIDescription("If checked, displays run info")
    val displayValidationRun: Boolean = false,
) : WidgetConfig

data class BranchStatusesWidgetConfigBranch(
    val project: String,
    val branch: String,
)
