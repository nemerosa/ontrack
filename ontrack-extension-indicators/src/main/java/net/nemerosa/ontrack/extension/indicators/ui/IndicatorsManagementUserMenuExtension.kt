package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

/**
 * User menu to access the management of indicators.
 */
@Component
class IndicatorsManagementUserMenuExtension(
    extension: IndicatorsExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extension), UserMenuExtension {

    /**
     * If the user is authorized to more functions than only the access to the portfolios, we
     * provide an access to the indicators management. If not, to the portfolios only.
     */
    override fun getAction(): Action =
        if (securityService.isGlobalFunctionGranted(IndicatorTypeManagement::class.java) || securityService.isGlobalFunctionGranted(IndicatorViewManagement::class.java)) {
            Action.of("indicators-management", "Indicators", "indicators-management")
        } else {
            Action.of("indicators-portfolios", "Indicators portfolios", "portfolios")
        }

    override fun getGlobalFunction(): Class<out GlobalFunction> = IndicatorPortfolioAccess::class.java

}