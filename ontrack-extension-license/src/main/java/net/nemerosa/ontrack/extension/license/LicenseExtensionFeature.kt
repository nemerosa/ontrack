package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class LicenseExtensionFeature : AbstractExtensionFeature(
    "license",
    "License",
    "License management",
    ExtensionFeatureOptions.DEFAULT.withGui(true)
)
