package net.nemerosa.ontrack.extension.guest

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class GuestExtensionFeature : AbstractExtensionFeature(
    id = "guest",
    name = "Guest account",
    description = "Creation of a guest account",
    options = ExtensionFeatureOptions.DEFAULT,
)