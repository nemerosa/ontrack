package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

/**
 * User menu to access the list of configurable indicators.
 */
@Component
class ConfigurableIndicatorsUserMenuExtension(
    extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), UserMenuExtension {

    override fun getAction(): Action =
        Action.of("configurable-indicators", "Configurable indicators", "configurable-indicators")

    override fun getGlobalFunction(): Class<out GlobalFunction> = IndicatorTypeManagement::class.java
}