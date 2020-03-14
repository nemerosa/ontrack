package net.nemerosa.ontrack.extension.general

import com.nhaarman.mockitokotlin2.mock
import net.nemerosa.ontrack.extension.general.MetaInfoPropertyItem.Companion.of
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import org.junit.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetaInfoPropertyTypeTest {

    private val type = MetaInfoPropertyType(
            extensionFeature = GeneralExtensionFeature(),
            searchIndexService = mock(),
            metaInfoSearchExtension = MetaInfoSearchExtension(
                    extensionFeature = GeneralExtensionFeature(),
                    uriBuilder = MockURIBuilder(),
                    propertyService = mock(),
                    structureService = mock(),
                    securityService = mock()
            )
    )

    @Test
    fun containsValueNOKIfWrongFormat() {
        assertFalse(type.containsValue(MetaInfoProperty(listOf(
                of("name", "value")
        )), "value"))
    }

    @Test
    fun containsValueNOKIfNotFound() {
        assertFalse(type.containsValue(MetaInfoProperty(listOf(
                of("name", "value1")
        )), "name:value"))
    }

    @Test
    fun containsValueOKIfFound() {
        assertTrue(type.containsValue(MetaInfoProperty(listOf(
                of("name", "value1")
        )), "name:value1"))
    }

    @Test
    fun containsValueOKIfFoundAmongOthers() {
        assertTrue(type.containsValue(MetaInfoProperty(Arrays.asList(
                of("name1", "value1"),
                of("name2", "value2")
        )), "name2:value2"))
    }
}