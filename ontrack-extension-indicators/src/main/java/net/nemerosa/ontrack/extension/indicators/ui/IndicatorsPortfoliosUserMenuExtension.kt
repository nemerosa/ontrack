package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class IndicatorsPortfoliosUserMenuExtension(
    extension: IndicatorsExtensionFeature,
) : AbstractExtension(extension), UserMenuExtension {

    override val action: Action =
        Action.of("indicators-portfolios", "Portfolios", "portfolios")
            .withGroup(IndicatorsUserMenuExtensionGroups.indicators)

    override val globalFunction: Class<out GlobalFunction> = IndicatorPortfolioAccess::class.java

}