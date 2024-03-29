package net.nemerosa.ontrack.extension.tfc

import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.extension.hook.HookExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class TFCExtensionFeature(
    cascExtensionFeature: CascExtensionFeature,
    generalExtensionFeature: GeneralExtensionFeature,
    hookExtensionFeature: HookExtensionFeature,
) : AbstractExtensionFeature(
    "tfc",
    "Terraform Cloud",
    "Support for Terraform Cloud notifications",
    ExtensionFeatureOptions.DEFAULT
        .withDependency(cascExtensionFeature)
        .withDependency(generalExtensionFeature)
        .withDependency(hookExtensionFeature)
        .withGui(true)
)
