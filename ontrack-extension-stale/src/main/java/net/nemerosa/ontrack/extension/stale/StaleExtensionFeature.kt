package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class StaleExtensionFeature : AbstractExtensionFeature(
        "stale",
        "Stale branches",
        "Disabling and deleting stale branches",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
)