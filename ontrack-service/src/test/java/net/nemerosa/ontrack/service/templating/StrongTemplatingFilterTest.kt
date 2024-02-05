package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StrongTemplatingFilterTest {

    private val filter = StrongTemplatingFilter()
    private val renderer = HtmlNotificationEventRenderer(OntrackConfigProperties())

    @Test
    fun id() {
        assertEquals("strong", filter.id)
    }

    @Test
    fun apply() {
        assertEquals("<b>some value</b>", filter.apply("some value", renderer))
    }

}