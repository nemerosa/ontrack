package net.nemerosa.ontrack.service.templating

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class URLEncodeTemplatingFilterTest {

    private val filter = URLEncodeTemplatingFilter()

    @Test
    fun id() {
        assertEquals("urlencode", filter.id)
    }

    @Test
    fun apply() {
        assertEquals("release%2F1.27", filter.apply("release/1.27"))
    }
    
}