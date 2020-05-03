package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.oidc.OIDCExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class OIDCSettingsUserMenuExtension(
        extensionFeature: OIDCExtensionFeature
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override fun getAction(): Action =
            Action.of("oidc-settings", "OIDC providers", "oidc-settings")

    override fun getGlobalFunction(): Class<out GlobalFunction> =
            GlobalSettings::class.java
}