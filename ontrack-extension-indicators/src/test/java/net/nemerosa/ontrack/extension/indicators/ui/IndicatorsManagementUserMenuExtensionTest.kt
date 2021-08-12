package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.model.support.ActionType
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorsManagementUserMenuExtensionTest {

    @Test
    fun check() {
        val extension = IndicatorsManagementUserMenuExtension(IndicatorsTestFixtures.indicatorsExtensionFeature())
        assertEquals(IndicatorPortfolioAccess::class.java, extension.globalFunction)
        assertEquals("indicators-management", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

}