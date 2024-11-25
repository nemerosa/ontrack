package net.nemerosa.ontrack.extension.environments.ui.widgets

import net.nemerosa.ontrack.model.dashboards.widgets.WidgetConfig

data class EnvironmentListWidgetConfig(
    val title: String,
    val tags: List<String>,
    val projects: List<String>,
) : WidgetConfig {

    companion object {
        fun default() = EnvironmentListWidgetConfig(
            title = "",
            tags = emptyList(),
            projects = emptyList(),
        )
    }

}
