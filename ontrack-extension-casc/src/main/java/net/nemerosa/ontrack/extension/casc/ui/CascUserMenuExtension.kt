package net.nemerosa.ontrack.extension.casc.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.casc.CascExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class CascUserMenuExtension(
    extensionFeature: CascExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override fun getAction() = Action(
        id = "casc-control",
        name = "Configuration as Code",
        type = ActionType.LINK,
        uri = "casc-control"
    )

    override fun getGlobalFunction(): Class<out GlobalFunction> = GlobalSettings::class.java
}