package net.nemerosa.ontrack.model.support.tree.support

import net.nemerosa.ontrack.model.support.tree.support.Markup.Companion.text
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MarkupTest {
    @Test
    fun text() {
        val m = text("Test")
        assertNull(m.type)
        assertEquals("Test", m.text)
        assertNull(m.attributes)
    }
}
