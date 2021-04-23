package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class OIDCExtensionFeature(
    private val cascExtensionFeature: CascExtensionFeature,
) : AbstractExtensionFeature(
    "oidc",
    "OIDC",
    "Support for OIDC authentication",
    ExtensionFeatureOptions.DEFAULT
        .withDependency(cascExtensionFeature)
        .withGui(true)
)