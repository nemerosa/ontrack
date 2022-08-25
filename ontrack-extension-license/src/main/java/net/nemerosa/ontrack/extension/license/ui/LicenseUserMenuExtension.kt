package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.license.LicenseExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class LicenseUserMenuExtension(extensionFeature: LicenseExtensionFeature) : AbstractExtension(extensionFeature),
    UserMenuExtension {

    override fun getGlobalFunction(): Class<out GlobalFunction>? = null

    override fun getAction() = Action(
        "license-info",
        "License information",
        ActionType.LINK,
        "info"
    )

}