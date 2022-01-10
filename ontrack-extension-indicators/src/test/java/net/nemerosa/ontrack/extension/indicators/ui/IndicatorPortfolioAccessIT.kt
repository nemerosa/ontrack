package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor
import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import net.nemerosa.ontrack.model.security.Roles
import org.junit.Test
import kotlin.test.assertTrue

class IndicatorPortfolioAccessIT : AbstractDSLTestJUnit4Support() {

    @Test
    fun `Administrators have access to the portfolios`() {
        asAdmin {
            assertTrue(securityService.isGlobalFunctionGranted(IndicatorPortfolioAccess::class.java))
        }
    }

    @Test
    fun `Portfolio managers have access to the portfolios`() {
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_PORTFOLIO_MANAGER) {
            assertTrue(securityService.isGlobalFunctionGranted(IndicatorPortfolioAccess::class.java))
        }
    }

    @Test
    fun `Indicator managers have access to the portfolios`() {
        asAccountWithGlobalRole(IndicatorRoleContributor.GLOBAL_INDICATOR_MANAGER) {
            assertTrue(securityService.isGlobalFunctionGranted(IndicatorPortfolioAccess::class.java))
        }
    }

    @Test
    fun `Read-only users have access to the portfolios`() {
        asAccountWithGlobalRole(Roles.GLOBAL_READ_ONLY) {
            assertTrue(securityService.isGlobalFunctionGranted(IndicatorPortfolioAccess::class.java))
        }
    }

}