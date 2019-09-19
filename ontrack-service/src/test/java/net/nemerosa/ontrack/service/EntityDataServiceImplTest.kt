package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.repository.EntityDataRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.test.*

class EntityDataServiceImplTest {

    private lateinit var service: EntityDataServiceImpl
    private lateinit var repository: EntityDataRepository
    private lateinit var securityService: SecurityService
    private lateinit var project: Project

    @Before
    fun setup() {
        repository = mock(EntityDataRepository::class.java)
        securityService = mock(SecurityService::class.java)
        service = EntityDataServiceImpl(repository, securityService)
        project = Project.of(NameDescription.nd("P", "Project")).withId(ID.of(1))
    }

    @Test
    fun `Getting a false boolean`() {
        `when`(repository.retrieve(project, "Test")).thenReturn("false")
        assertNotNull(service.retrieveBoolean(project, "Test")) { assertFalse(it) }
    }

    @Test
    fun `Getting a true boolean`() {
        `when`(repository.retrieve(project, "Test")).thenReturn("true")
        assertNotNull(service.retrieveBoolean(project, "Test")) { assertTrue(it) }
    }

    @Test
    fun `Not getting a boolean`() {
        `when`(repository.retrieve(project, "Test")).thenReturn(null)
        assertNull(service.retrieveBoolean(project, "Test"))
    }

    @Test
    fun `Getting an integer`() {
        `when`(repository.retrieve(project, "Test")).thenReturn("10")
        assertEquals(10, service.retrieveInteger(project, "Test"))
    }

    @Test
    fun `Not getting an integer`() {
        `when`(repository.retrieve(project, "Test")).thenReturn(null)
        assertNull(service.retrieveInteger(project, "Test"))
    }

    @Test
    fun `Getting a string`() {
        `when`(repository.retrieve(project, "Test")).thenReturn("Value")
        assertEquals("Value", service.retrieve(project, "Test"))
    }

    @Test
    fun `Not getting a string`() {
        `when`(repository.retrieve(project, "Test")).thenReturn(null)
        assertNull(service.retrieve(project, "Test"))
    }

    @Test
    fun `Getting a JSON`() {
        `when`(repository.retrieveJson(project, "Test")).thenReturn(JsonUtils.parseAsNode("""{"name":"Name","value":"Value"}"""))
        val json = JsonUtils.toMap(service.retrieveJson(project, "Test"))
        assertEquals("Name", json["name"])
        assertEquals("Value", json["value"])
    }

    @Test
    fun `Not getting a JSON`() {
        `when`(repository.retrieve(project, "Test")).thenReturn(null)
        assertNull(service.retrieveJson(project, "Test"))
    }

    @Test
    fun `Getting an Object`() {
        `when`(repository.retrieveJson(project, "Test")).thenReturn(JsonUtils.parseAsNode("""{"name":"Name","value":"Value"}"""))
        val o = service.retrieve(project, "Test", NameValue::class.java)
        assertNotNull(o) {
            assertEquals("Name", it.name)
            assertEquals("Value", it.value)
        }
    }

    @Test
    fun `Not getting an Object`() {
        `when`(repository.retrieve(project, "Test")).thenReturn(null)
        assertNull(service.retrieve(project, "Test", NameValue::class.java))
    }

    @Test
    fun `Store a boolean`() {
        service.store(project, "Test", true)
        verify(repository).store(project, "Test", "true")
    }

    @Test
    fun `Store an integer`() {
        service.store(project, "Test", 10)
        verify(repository).store(project, "Test", "10")
    }

    @Test
    fun `Store a string`() {
        service.store(project, "Test", "Value")
        verify(repository).store(project, "Test", "Value")
    }

    @Test
    fun `Store an object`() {
        service.store(project, "Test", NameValue("Name", "Value"))
        verify(repository).storeJson(project, "Test", JsonUtils.parseAsNode("""{"name":"Name","value":"Value"}"""))
    }

    @Test
    fun `Deleting an object`() {
        service.delete(project, "Test")
        verify(repository).delete(project, "Test")
    }

}
