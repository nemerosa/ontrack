package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioAccess
import net.nemerosa.ontrack.model.support.ActionType
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorPortfolioUserMenuExtensionTest {

    @Test
    fun check() {
        val extension = IndicatorPortfolioUserMenuExtension(IndicatorsTestFixtures.indicatorsExtensionFeature())
        assertEquals(IndicatorPortfolioAccess::class.java, extension.globalFunction)
        assertEquals("portfolios", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

}