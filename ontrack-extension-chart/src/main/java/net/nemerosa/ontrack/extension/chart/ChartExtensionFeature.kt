package net.nemerosa.ontrack.extension.chart

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class ChartExtensionFeature : AbstractExtensionFeature(
    id = "chart",
    name = "Charts",
    description = "Support for chart data",
    options = ExtensionFeatureOptions.DEFAULT,
)
