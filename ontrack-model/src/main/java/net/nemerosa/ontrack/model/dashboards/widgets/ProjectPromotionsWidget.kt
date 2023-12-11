package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class ProjectPromotionsWidget : AbstractWidget<ProjectPromotionsWidget.ProjectPromotionsWidgetConfig>(
    key = "home/ProjectPromotion",
    name = "Project promotions",
    description = "For a list of promotions inside the same project, displays the last build per promotion, its own promotions, decorations and its list of dependencies, filtered by label",
    defaultConfig = ProjectPromotionsWidgetConfig(project = null),
    preferredHeight = 6,
) {
    data class ProjectPromotionsWidgetConfig(
        val project: String?,
        val promotions: List<String> = emptyList(),
        val depth: Int? = null,
        val label: String? = null,
    ) : WidgetConfig
}
