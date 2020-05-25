package net.nemerosa.ontrack.extension.indicators

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class IndicatorsExtensionFeature : AbstractExtensionFeature(
        "indicators",
        "Project indicators",
        "Management of indicators, manual and/or automated, at project level.",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
)
