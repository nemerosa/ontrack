package net.nemerosa.ontrack.extension.environments.ui.widgets

import net.nemerosa.ontrack.model.dashboards.widgets.WidgetConfig

data class EnvironmentWidgetConfig(
    val name: String,
    val projects: List<String>,
) : WidgetConfig {

    companion object {
        fun default() = EnvironmentWidgetConfig(
            name = "",
            projects = emptyList(),
        )
    }

}
