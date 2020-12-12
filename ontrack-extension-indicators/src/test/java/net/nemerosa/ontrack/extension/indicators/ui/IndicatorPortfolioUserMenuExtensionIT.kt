package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.Roles
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class IndicatorPortfolioUserMenuExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var extension: IndicatorPortfolioUserMenuExtension

    @Test
    fun `Administrators have access to the portfolios`() {
        asAdmin {
            assertTrue(securityService.isGlobalFunctionGranted(extension.globalFunction))
        }
    }

    @Test
    fun `Portfolio managers have access to the portfolios`() {
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_PORTFOLIO_MANAGER) {
            assertTrue(securityService.isGlobalFunctionGranted(extension.globalFunction))
        }
    }

    @Test
    fun `Indicator managers have access to the portfolios`() {
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_MANAGER) {
            assertTrue(securityService.isGlobalFunctionGranted(extension.globalFunction))
        }
    }

    @Test
    fun `Read-only users have access to the portfolios`() {
        asAccountWithGlobalRole(Roles.GLOBAL_READ_ONLY) {
            assertTrue(securityService.isGlobalFunctionGranted(extension.globalFunction))
        }
    }

}