package net.nemerosa.ontrack.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.repository.EntityDataRepository
import org.junit.jupiter.api.BeforeEach
import kotlin.test.*

class EntityDataServiceImplTest {

    private lateinit var service: EntityDataServiceImpl
    private lateinit var repository: EntityDataRepository
    private val securityService: SecurityService = MockSecurityService()
    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        repository = mockk<EntityDataRepository>(relaxed = true)
        service = EntityDataServiceImpl(repository, securityService)
        project = Project.of(NameDescription.nd("P", "Project")).withId(ID.of(1))
    }

    @Test
    fun `Testing if data is associated with a project`() {
        every { repository.hasEntityValue(project, "Key") } returns false
        assertFalse(service.hasEntityValue(project, "Key"))
        every { repository.hasEntityValue(project, "Key") } returns true
        assertTrue(service.hasEntityValue(project, "Key"))
    }

    @Test
    fun `Getting a false boolean`() {
        every { repository.retrieve(project, "Test") } returns "false"
        assertNotNull(service.retrieveBoolean(project, "Test")) { assertFalse(it) }
    }

    @Test
    fun `Getting a true boolean`() {
        every { repository.retrieve(project, "Test") } returns "true"
        assertNotNull(service.retrieveBoolean(project, "Test")) { assertTrue(it) }
    }

    @Test
    fun `Not getting a boolean`() {
        every { repository.retrieve(project, "Test") } returns null
        assertNull(service.retrieveBoolean(project, "Test"))
    }

    @Test
    fun `Getting an integer`() {
        every { repository.retrieve(project, "Test") } returns "10"
        assertEquals(10, service.retrieveInteger(project, "Test"))
    }

    @Test
    fun `Not getting an integer`() {
        every { repository.retrieve(project, "Test") } returns null
        assertNull(service.retrieveInteger(project, "Test"))
    }

    @Test
    fun `Getting a string`() {
        every { repository.retrieve(project, "Test") } returns "Value"
        assertEquals("Value", service.retrieve(project, "Test"))
    }

    @Test
    fun `Not getting a string`() {
        every { repository.retrieve(project, "Test") } returns null
        assertNull(service.retrieve(project, "Test"))
    }

    @Test
    fun `Getting a JSON`() {
        every {
            repository.retrieveJson(
                project,
                "Test"
            )
        } returns JsonUtils.parseAsNode("""{"name":"Name","value":"Value"}""")
        val json = JsonUtils.toMap(service.retrieveJson(project, "Test"))
        assertEquals("Name", json["name"])
        assertEquals("Value", json["value"])
    }

    @Test
    fun `Not getting a JSON`() {
        every { repository.retrieveJson(project, "Test") } returns null
        assertNull(service.retrieveJson(project, "Test"))
    }

    @Test
    fun `Getting an Object`() {
        every {
            repository.retrieveJson(
                project,
                "Test"
            )
        } returns JsonUtils.parseAsNode("""{"name":"Name","value":"Value"}""")
        val o = service.retrieve(project, "Test", NameValue::class.java)
        assertNotNull(o) {
            assertEquals("Name", it.name)
            assertEquals("Value", it.value)
        }
    }

    @Test
    fun `Not getting an Object`() {
        every { repository.retrieveJson(project, "Test") } returns null
        assertNull(service.retrieve(project, "Test", NameValue::class.java))
    }

    @Test
    fun `Store a boolean`() {
        service.store(project, "Test", true)
        verify {
            repository.store(project, "Test", "true")
        }
    }

    @Test
    fun `Store an integer`() {
        service.store(project, "Test", 10)
        verify {
            repository.store(project, "Test", "10")
        }
    }

    @Test
    fun `Store a string`() {
        service.store(project, "Test", "Value")
        verify {
            repository.store(project, "Test", "Value")
        }
    }

    @Test
    fun `Store an object`() {
        service.store(project, "Test", NameValue("Name", "Value"))
        verify {
            repository.storeJson(project, "Test", JsonUtils.parseAsNode("""{"name":"Name","value":"Value"}"""))
        }
    }

    @Test
    fun `Deleting an object`() {
        service.delete(project, "Test")
        verify {
            repository.delete(project, "Test")
        }
    }

}
