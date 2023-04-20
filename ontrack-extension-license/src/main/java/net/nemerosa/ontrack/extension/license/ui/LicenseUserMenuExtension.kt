package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.license.LicenseExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component

@Component
class LicenseUserMenuExtension(extensionFeature: LicenseExtensionFeature) : AbstractExtension(extensionFeature),
    UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction>? = null

    override val action = Action(
        id = "license-info",
        name = "License information",
        type = ActionType.LINK,
        uri = "info",
        group = UserMenuExtensionGroups.system,
    )

}