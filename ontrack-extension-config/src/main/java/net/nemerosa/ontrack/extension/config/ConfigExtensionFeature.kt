package net.nemerosa.ontrack.extension.config

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class ConfigExtensionFeature : AbstractExtensionFeature(
    id = "config",
    name = "Configuration",
    description = "Injection of configuration",
    options = ExtensionFeatureOptions.DEFAULT.withGui(true),
)
