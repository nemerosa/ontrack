package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class HookExtensionFeature : AbstractExtensionFeature(
    "hook",
    "Hooks",
    "Framework to manage hooks in other extensions",
    ExtensionFeatureOptions.DEFAULT
            .withGui(true)
)
