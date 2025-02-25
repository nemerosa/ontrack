package net.nemerosa.ontrack.extension.environments.ui.widgets

import net.nemerosa.ontrack.model.dashboards.widgets.AbstractWidget
import org.springframework.stereotype.Component

@Component
class EnvironmentListWidget : AbstractWidget<EnvironmentListWidgetConfig>(
    key = "extension/environments/EnvironmentList",
    name = "Environments list",
    description = "List of environments, their slots & their deployed pipelines",
    defaultConfig = EnvironmentListWidgetConfig.default(),
    preferredHeight = 40,
)
