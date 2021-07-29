package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class IndicatorViewManagementUserMenuExtension(
    extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), UserMenuExtension {

    override fun getAction(): Action =
        Action.of("views", "Indicators views", "views")

    override fun getGlobalFunction(): Class<out GlobalFunction> =
        IndicatorViewManagement::class.java
}