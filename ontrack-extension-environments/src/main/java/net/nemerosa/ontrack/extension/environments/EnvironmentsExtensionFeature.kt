package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class EnvironmentsExtensionFeature : AbstractExtensionFeature(
    id = "environments",
    name = "Environments",
    description = "Management of deployments in environments",
    options = ExtensionFeatureOptions.DEFAULT.withGui(true),
)
