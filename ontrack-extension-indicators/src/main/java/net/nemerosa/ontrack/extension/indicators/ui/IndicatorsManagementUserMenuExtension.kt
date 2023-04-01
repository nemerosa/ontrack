package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

/**
 * User menu to access the management of indicators.
 */
@Component
@Deprecated("Will be split into different entries in a common group")
class IndicatorsManagementUserMenuExtension(
    extension: IndicatorsExtensionFeature,
) : AbstractExtension(extension), UserMenuExtension {

    override val action: Action =
        Action.of("indicators-management", "Indicators", "indicators-management")

    override val globalFunction: Class<out GlobalFunction> = IndicatorPortfolioAccess::class.java

}