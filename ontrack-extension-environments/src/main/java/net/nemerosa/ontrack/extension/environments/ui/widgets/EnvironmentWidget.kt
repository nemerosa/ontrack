package net.nemerosa.ontrack.extension.environments.ui.widgets

import net.nemerosa.ontrack.model.dashboards.widgets.AbstractWidget
import org.springframework.stereotype.Component

@Component
class EnvironmentWidget : AbstractWidget<EnvironmentWidgetConfig>(
    key = "extension/environments/Environment",
    name = "Environment",
    description = "List of slots for an environment and their pipelines (deployed & current)",
    defaultConfig = EnvironmentWidgetConfig.default(),
    preferredHeight = 40,
)
