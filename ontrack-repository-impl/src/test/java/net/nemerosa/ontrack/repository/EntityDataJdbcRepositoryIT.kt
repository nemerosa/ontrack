package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Transactional
class EntityDataJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var repository: EntityDataRepository
    private lateinit var project: Project

    @Before
    fun create_project() {
        project = do_create_project()
    }

    @Test
    fun save_retrieve_delete_data() {
        val key = "Test 1"
        assertNull(repository.retrieve(project, key))
        repository.store(project, key, "Value 1")
        assertEquals(repository.retrieve(project, key), "Value 1")
        repository.delete(project, key)
        assertNull(repository.retrieve(project, key))
    }

    @Test
    fun save_update_data() {
        val key = "Test 2"
        assertNull(repository.retrieve(project, key))
        repository.store(project, key, "Value 1")
        assertEquals(repository.retrieve(project, key), "Value 1")
        repository.store(project, key, "Value 2")
        assertEquals(repository.retrieve(project, key), "Value 2")
    }

    @Test
    fun save_update_json_data() {
        val key = "Test 3"
        assertNull(repository.retrieveJson(project, key))
        repository.storeJson(project, key, JsonUtils.format(TestObject("Value 1")))
        assertEquals(TestObject("Value 1"), JsonUtils.parse(repository.retrieveJson(project, key), TestObject::class.java))
        repository.storeJson(project, key, JsonUtils.format(TestObject("Value 2")))
        assertEquals(TestObject("Value 2"), JsonUtils.parse(repository.retrieveJson(project, key), TestObject::class.java))
    }

    @Test
    fun `Testing if an entity is linked to some data`() {
        val key = uid("K")
        assertFalse(repository.hasEntityValue(project, key), "No data associated")
        repository.store(project, key, "Some data")
        assertTrue(repository.hasEntityValue(project, key), "Some data associated")
    }
}