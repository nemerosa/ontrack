package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

/**
 * User menu to access the list of portfolios.
 */
@Component
class IndicatorPortfolioUserMenuExtension(
        extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), UserMenuExtension {

    override fun getAction(): Action =
            Action.of("portfolios", "Indicators portfolios", "portfolios")

    override fun getGlobalFunction(): Class<out GlobalFunction> = IndicatorPortfolioAccess::class.java
}