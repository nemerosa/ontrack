package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class CascExtensionFeature : AbstractExtensionFeature(
    "casc",
    "CasC",
    "Configuration as Code",
    ExtensionFeatureOptions.DEFAULT.withGui(true)
)
