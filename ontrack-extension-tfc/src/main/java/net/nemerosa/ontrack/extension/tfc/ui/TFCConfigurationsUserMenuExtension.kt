package net.nemerosa.ontrack.extension.tfc.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.tfc.TFCExtensionFeature
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class TFCConfigurationsUserMenuExtension(
    extensionFeature: TFCExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction> = GlobalSettings::class.java

    override val action: Action =
        Action.of("tfc-configurations", "TFC configurations", "configurations")
                .withGroup(UserMenuExtensionGroups.configuration)

}