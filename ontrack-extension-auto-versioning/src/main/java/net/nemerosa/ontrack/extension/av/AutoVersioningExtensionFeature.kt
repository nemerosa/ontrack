package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class AutoVersioningExtensionFeature(
    scmExtensionFeature: SCMExtensionFeature,
) : AbstractExtensionFeature(
    "auto-versioning",
    "Auto versioning",
    "Auto versioning on promotion",
    ExtensionFeatureOptions.DEFAULT
        .withDependency(scmExtensionFeature)
        .withGui(true)
)
