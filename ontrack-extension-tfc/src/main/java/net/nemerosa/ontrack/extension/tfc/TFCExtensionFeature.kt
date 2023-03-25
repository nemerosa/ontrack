package net.nemerosa.ontrack.extension.tfc

import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class TFCExtensionFeature(
    cascExtensionFeature: CascExtensionFeature,
) : AbstractExtensionFeature(
    "tfc",
    "Terraform Cloud",
    "Support for Terraform Cloud notifications",
    ExtensionFeatureOptions.DEFAULT
        .withDependency(cascExtensionFeature)
        .withGui(true)
)
