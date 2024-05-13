package net.nemerosa.ontrack.extension.workflows

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class WorkflowsExtensionFeature : AbstractExtensionFeature(
    "workflows",
    "Workflows",
    "Definition of workflows",
    ExtensionFeatureOptions.DEFAULT
)
