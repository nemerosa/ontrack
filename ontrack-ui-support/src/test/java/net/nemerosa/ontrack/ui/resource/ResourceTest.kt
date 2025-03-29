package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResourceTest : AbstractResourceTest() {
    @Test
    fun resource_to_json() {
        val info = Dummy("1.0.0")
        val resource =
            Resource.of(info, URI.create("http://host/dummy")).with("connectors", URI.create("http://host/dummy/test"))
        assertResourceJson(
            mapper,
            mapOf(
                "_self" to "http://host/dummy",
                "version" to "1.0.0",
                "connectors" to "http://host/dummy/test",
            ).asJson(),
            resource
        )
    }

    @Test
    fun resource_not_null() {
        assertFailsWith<NullPointerException> {
            Resource.of<String?>(null, URI.create(""))
        }
    }

    @Test
    fun container_first() {
        assertEquals(String::class.java, Resource.of("Test", URI.create("")).viewType)
    }
}
