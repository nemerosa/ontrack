package net.nemerosa.ontrack.boot.support

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.boot.ui.AbstractWebTestSupport
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceModule
import net.nemerosa.ontrack.ui.resource.ResourceDecorator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpOutputMessage
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class ResourceHttpMessageConverterIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var decorators: Collection<ResourceDecorator<*>>

    private lateinit var converter: ResourceHttpMessageConverter

    @BeforeEach
    fun before() {
        converter = ResourceHttpMessageConverter(
            MockURIBuilder(), securityService, listOf(DefaultResourceModule(decorators))
        )
    }

    @Test
    @Throws(Exception::class)
    fun branch_disable_granted_for_automation() { // Objects
        val p = Project.of(NameDescription("P", "Projet créé")).withId(ID.of(1))
            .withSignature(TestFixtures.SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1))
            .withSignature(TestFixtures.SIGNATURE)
        // Message
        val message = mockk<HttpOutputMessage>()
        val output = ByteArrayOutputStream()
        every { message.body } returns output
        // Serialization
        asGlobalRole("AUTOMATION").execute { converter.writeInternal(b, message) }
        // Content
        val json = output.toByteArray().toString(Charsets.UTF_8)
        // Parsing
        val node = ObjectMapperFactory.create().readTree(json)
        // Disable link
        assertEquals(
            "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#disableBranch:1",
            node.path("_disable").asText()
        )
    }

    @Test
    @Throws(Exception::class)
    fun branch_enable_granted_for_automation() { // Objects
        val p = Project.of(NameDescription("P", "Projet créé")).withId(ID.of(1))
            .withSignature(TestFixtures.SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1))
            .withDisabled(true)
            .withSignature(TestFixtures.SIGNATURE)
        // Message
        val message = mockk<HttpOutputMessage>()
        val output = ByteArrayOutputStream()
        every { message.body } returns output
        // Serialization
        asGlobalRole("AUTOMATION").execute { converter.writeInternal(b, message) }
        // Content
        val json = output.toByteArray().toString(Charsets.UTF_8)
        // Parsing
        val node = ObjectMapperFactory.create().readTree(json)
        // Enable link
        assertEquals(
            "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#enableBranch:1",
            node.path("_enable").asText()
        )
    }
}