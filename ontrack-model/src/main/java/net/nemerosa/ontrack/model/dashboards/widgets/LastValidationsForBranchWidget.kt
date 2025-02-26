package net.nemerosa.ontrack.model.dashboards.widgets

import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

@Component
class LastValidationsForBranchWidget : AbstractWidget<LastValidationsForBranchWidgetConfig>(
    key = "home/LastValidationsForBranch",
    name = "Last validations for a branch",
    description = "Displays the last validations for a branch",
    defaultConfig = LastValidationsForBranchWidgetConfig(),
    preferredHeight = 45,
)

data class LastValidationsForBranchWidgetConfig(
    @APIDescription("Overriding the title of the widget")
    val title: String? = null,
    @APIDescription("Name of the project")
    val project: String? = null,
    @APIDescription("Name of the branch")
    val branch: String? = null,
    @APIDescription("List of validations to display")
    val validations: List<String> = emptyList(),
    @APIDescription("If checked, displays the promotions for each build")
    val displayPromotions: Boolean = false,
) : WidgetConfig
