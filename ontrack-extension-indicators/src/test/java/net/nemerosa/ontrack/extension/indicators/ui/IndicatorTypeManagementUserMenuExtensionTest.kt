package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures.indicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.model.support.ActionType
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorTypeManagementUserMenuExtensionTest {

    @Test
    fun check() {
        val extension = IndicatorTypeManagementUserMenuExtension(indicatorsExtensionFeature())
        assertEquals(IndicatorTypeManagement::class.java, extension.globalFunction)
        assertEquals("types", extension.action.uri)
        assertEquals(ActionType.LINK, extension.action.type)
    }

}