package net.nemerosa.ontrack.extension.general

import org.junit.Assert.assertEquals
import org.junit.Test

class LinkPropertyTypeTest {

    private val type: LinkPropertyType = LinkPropertyType(
            GeneralExtensionFeature()
    )

    @Test
    fun replacement() {
        val property = LinkProperty.of("test", "http://wiki/P1")
        assertEquals(
                LinkProperty.of("test", "http://wiki/P2"),
                type.replaceValue(property) { s -> s.replace("P1".toRegex(), "P2") }
        )
    }

}