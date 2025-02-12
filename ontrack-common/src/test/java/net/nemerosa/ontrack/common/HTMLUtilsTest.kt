package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HTMLUtilsTest {

    @Test
    fun `Sanitizing HTML`() {
        assertEquals("Some value", "Some value".safeHtml)
        assertEquals("Some <b>bold</b> value", "Some <b>bold</b> value".safeHtml)
        assertEquals("Some &lt;script&gt;forbidden&lt;/script&gt; value", "Some <script>forbidden</script> value".safeHtml)
        assertEquals("Some &lt;real&gt; <i>value</i>", "Some <real> <i>value</i>".safeHtml)
        assertEquals(
            """My content with a <a href="https://ontrack.test.com">link</a> and something &lt;wrong&gt;""",
            """My content with a <a href="https://ontrack.test.com">link</a> and something <wrong>""".safeHtml
        )
    }

}