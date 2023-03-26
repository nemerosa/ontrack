package net.nemerosa.ontrack.extension.tfc.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
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

    override fun getGlobalFunction(): Class<out GlobalFunction> = GlobalSettings::class.java

    override fun getAction(): Action =
        Action.of("tfc-configurations", "TFC configurations", "configurations")

}