package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.model.support.ActionType
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorCategoryManagementUserMenuExtensionTest {

    @Test
    fun check() {
        val extension = IndicatorCategoryManagementUserMenuExtension(IndicatorsTestFixtures.indicatorsExtensionFeature())
        assertEquals(IndicatorTypeManagement::class.java, extension.globalFunction)
        assertEquals("categories", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

}