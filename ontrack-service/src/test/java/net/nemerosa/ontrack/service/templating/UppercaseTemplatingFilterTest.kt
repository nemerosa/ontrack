package net.nemerosa.ontrack.service.templating

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UppercaseTemplatingFilterTest {

    private val filter = UppercaseTemplatingFilter()

    @Test
    fun id() {
        assertEquals("uppercase", filter.id)
    }

    @Test
    fun apply() {
        assertEquals("PROJECT", filter.apply("Project"))
    }

}