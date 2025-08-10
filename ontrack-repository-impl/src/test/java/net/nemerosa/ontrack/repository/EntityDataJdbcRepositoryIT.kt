package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

    @BeforeEach
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
        repository.storeJson(project, key, TestObject("Value 1").asJson())
        assertEquals(TestObject("Value 1"), repository.retrieveJson(project, key)?.parse<TestObject>())
        repository.storeJson(project, key, TestObject("Value 2").asJson())
        assertEquals(TestObject("Value 2"), repository.retrieveJson(project, key)?.parse<TestObject>())
    }

    @Test
    fun `Testing if an entity is linked to some data`() {
        val key = uid("K")
        assertFalse(repository.hasEntityValue(project, key), "No data associated")
        repository.store(project, key, "Some data")
        assertTrue(repository.hasEntityValue(project, key), "Some data associated")
    }
}