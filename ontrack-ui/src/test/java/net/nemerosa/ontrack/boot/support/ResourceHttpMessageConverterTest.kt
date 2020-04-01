package net.nemerosa.ontrack.boot.support

import com.nhaarman.mockitokotlin2.mock
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceModule
import net.nemerosa.ontrack.ui.resource.Resource
import org.junit.Before
import org.junit.Test
import org.springframework.mock.http.MockHttpOutputMessage
import java.net.URI
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ResourceHttpMessageConverterTest {

    private lateinit var converter: ResourceHttpMessageConverter

    @Before
    fun before() {
        converter = ResourceHttpMessageConverter(
                MockURIBuilder(),
                mock(),
                listOf(DefaultResourceModule(emptyList()))
        )
    }

    @Test
    fun `Project raw`() {
        val project = Project.of(NameDescription("PRJ", "project"))
                .withId(ID.of(1))
                .withSignature(
                        Signature(
                                LocalDateTime.of(2020, 3, 29, 13, 28, 22),
                                User("test")
                        )
                )
        // Serialization
        val output = MockHttpOutputMessage()
        converter.writeInternal(project, output)
        // JSON
        val node = ObjectMapperFactory.create().readTree(output.bodyAsBytes)
        // Check
        assertEquals(
                mapOf(
                        "id" to 1,
                        "name" to "PRJ",
                        "description" to "project",
                        "disabled" to false,
                        "signature" to mapOf(
                                "time" to "2020-03-29T13:28:22Z",
                                "user" to mapOf("name" to "test")
                        )
                ).asJson(),
                node
        )
    }

    @Test
    fun `Project resource`() {
        val project = Project.of(NameDescription("PRJ", "project"))
                .withId(ID.of(1))
                .withSignature(
                        Signature(
                                LocalDateTime.of(2020, 3, 29, 13, 28, 22),
                                User("test")
                        )
                )
        // Resource
        val resource = Resource.of(project, URI.create("uri:project/1"))
        // Serialization
        val output = MockHttpOutputMessage()
        converter.writeInternal(resource, output)
        // JSON
        val node = ObjectMapperFactory.create().readTree(output.bodyAsBytes)
        // Check
        assertEquals(
                mapOf(
                        "id" to 1,
                        "name" to "PRJ",
                        "description" to "project",
                        "disabled" to false,
                        "signature" to mapOf(
                                "time" to "2020-03-29T13:28:22Z",
                                "user" to mapOf("name" to "test")
                        ),
                        "_self" to "uri:project/1"
                ).asJson(),
                node
        )
    }
}