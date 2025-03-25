package net.nemerosa.ontrack.model.dashboards.widgets

import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

@Component
class ValidationsLastPromotionBuildWidget : AbstractWidget<ValidationsLastPromotionBuildWidgetConfig>(
    key = "home/ValidationsLastPromotionBuild",
    name = "Validations of last promoted build",
    description = "Displays the validations for the last promoted build of a branch",
    defaultConfig = ValidationsLastPromotionBuildWidgetConfig(),
    preferredHeight = 45,
)

data class ValidationsLastPromotionBuildWidgetConfig(
    @APIDescription("Overriding the title of the widget")
    val title: String? = null,
    @APIDescription("Name of the project")
    val project: String? = null,
    @APIDescription("Name of the branch")
    val branch: String? = null,
    @APIDescription("Name of the promotion")
    val promotion: String? = null,
    @APIDescription("List of validations to display")
    val validations: List<String> = emptyList(),
) : WidgetConfig
