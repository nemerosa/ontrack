package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LowercaseTemplatingFilterTest {

    private val filter = LowercaseTemplatingFilter()

    @Test
    fun id() {
        assertEquals("lowercase", filter.id)
    }

    @Test
    fun apply() {
        assertEquals("project", filter.apply("Project", PlainEventRenderer.INSTANCE))
    }
    
}