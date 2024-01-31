package net.nemerosa.ontrack.service.templating

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
        assertEquals("project", filter.apply("Project"))
    }
    
}