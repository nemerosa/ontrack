package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

/**
 * Management of SonarQube configurations available in the user menu.
 */
@Component
class SonarQubeUserMenuExtension(feature: SonarQubeExtensionFeature) : AbstractExtension(feature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction> = GlobalSettings::class.java

    override val action: Action = Action.of("sonarqube-configurations", "SonarQube configurations", "configurations")
        .withGroup(UserMenuExtensionGroups.configuration)
}
