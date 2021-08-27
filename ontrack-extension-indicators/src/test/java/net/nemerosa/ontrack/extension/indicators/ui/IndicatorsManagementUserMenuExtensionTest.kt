package net.nemerosa.ontrack.extension.indicators.ui

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ActionType
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorsManagementUserMenuExtensionTest {

    private lateinit var securityService: SecurityService

    @Before
    fun before() {
        securityService = mockk()
    }

    @Test
    fun `Portfolios only`() {
        every { securityService.isGlobalFunctionGranted(IndicatorTypeManagement::class.java) } returns false
        every { securityService.isGlobalFunctionGranted(IndicatorViewManagement::class.java) } returns false
        val extension =
            IndicatorsManagementUserMenuExtension(IndicatorsTestFixtures.indicatorsExtensionFeature(), securityService)
        assertEquals(IndicatorPortfolioAccess::class.java, extension.globalFunction)
        assertEquals("portfolios", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

    @Test
    fun `Management when type management is granted`() {
        every { securityService.isGlobalFunctionGranted(IndicatorTypeManagement::class.java) } returns true
        every { securityService.isGlobalFunctionGranted(IndicatorViewManagement::class.java) } returns false
        val extension =
            IndicatorsManagementUserMenuExtension(IndicatorsTestFixtures.indicatorsExtensionFeature(), securityService)
        assertEquals(IndicatorPortfolioAccess::class.java, extension.globalFunction)
        assertEquals("indicators-management", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

    @Test
    fun `Management when view management is granted`() {
        every { securityService.isGlobalFunctionGranted(IndicatorTypeManagement::class.java) } returns false
        every { securityService.isGlobalFunctionGranted(IndicatorViewManagement::class.java) } returns true
        val extension =
            IndicatorsManagementUserMenuExtension(IndicatorsTestFixtures.indicatorsExtensionFeature(), securityService)
        assertEquals(IndicatorPortfolioAccess::class.java, extension.globalFunction)
        assertEquals("indicators-management", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

}